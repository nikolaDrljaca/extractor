package com.drbrosdev.extractor.ui.imageinfo.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.AnnotationType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.repository.LupaImageRepository
import com.drbrosdev.extractor.domain.repository.payload.EmbedUpdate
import com.drbrosdev.extractor.domain.usecase.suggestion.SuggestUserKeywords
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.shared.AppImageInfoHeader
import com.drbrosdev.extractor.ui.imageinfo.LupaImageHeaderState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Parcelize
data class EditLupaAnnotationsNavTarget(
    val mediaImageId: Long,
    val type: AnnotationType
) : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: EditLupaAnnotationsViewModel = koinViewModel {
            parametersOf(mediaImageId, type)
        }

        val header by viewModel.headerState.collectAsStateWithLifecycle()

        EditLupaAnnotationScreen(
            header = header,
            annotationType = type,
            userEditComponent = viewModel.userEditComponent
        )
    }
}

class EditLupaAnnotationsViewModel(
    private val mediaImageId: Long,
    private val stateHandle: SavedStateHandle,
    private val annotationType: AnnotationType,
    private val lupaImageRepository: LupaImageRepository,
    private val suggestUserKeywords: SuggestUserKeywords
) : ViewModel() {
    private val imageId = MediaImageId(mediaImageId)

    private val lupaImage = lupaImageRepository.findByIdAsFlow(MediaImageId(mediaImageId))
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            replay = 1
        )

    val headerState = lupaImage
        .map { it?.let { LupaImageHeaderState.fromMetadata(it.metadata) } }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            null
        )

    val userEditComponent by lazy {
        UserKeywordEditorComponentImpl(
            scope = viewModelScope,
            stateHandle = stateHandle,
            getSuggestions = { suggestUserKeywords.execute(imageId) },
            annotationsFlow = { lupaImage },
            createNewKeyword = ::createNewUserKeyword,
            deleteKeyword = ::deleteUserEmbed
        )
    }

    private fun deleteUserEmbed(value: String) {
        viewModelScope.launch {
            lupaImageRepository.deleteUserEmbed(
                mediaImageId = imageId,
                value = value
            )
        }
    }

    private fun createNewUserKeyword(value: String) {
        viewModelScope.launch {
            // fetch existing
            val id = MediaImageId(mediaImageId)
            val existing = lupaImageRepository.findImageDataByMediaId(id)
                .map { it?.userEmbeds }
                .first() ?: return@launch

            // parse input as CSV and map to new entries
            val newUserEmbed = value.split(",")
                .map { it.trim() }
                .distinct()

            // append to existing
            val updated = (newUserEmbed + existing)
                .map { EmbedUpdate(id, it) }

            // update
            lupaImageRepository.updateUserEmbed(updated)
        }
    }
}

sealed interface EditLupaAnnotationState {
    // visual
    @Immutable
    data class VisualKeywords(
        val keywords: List<String>,
        val onDelete: (String) -> Unit
    ) : EditLupaAnnotationState

    // text
    data class TextKeywords(
        private val allText: String
    ) : EditLupaAnnotationState {
        val textFieldState = TextFieldState(initialText = allText)
    }

    data object Loading : EditLupaAnnotationState
}

fun EditLupaAnnotationState.textChangesAsFlow(): Flow<String> {
    return when (this) {
        is EditLupaAnnotationState.TextKeywords -> snapshotFlow { textFieldState.text }
            .map { it.toString() }

        else -> emptyFlow()
    }
}

@Composable
fun EditLupaAnnotationScreen(
    modifier: Modifier = Modifier,
    header: LupaImageHeaderState?,
    annotationType: AnnotationType,
    userEditComponent: UserKeywordEditorComponent
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
            .systemBarsPadding(),
    ) {
        header?.let {
            AppImageInfoHeader(
                model = it
            )
        }

        Spacer(Modifier.height(24.dp))

        when (annotationType) {
            AnnotationType.USER -> UserKeyWordsEditor(
                component = userEditComponent
            )

            AnnotationType.VISUAL ->
                Box(Modifier)
//                VisualKeywordsEditor(
//                    state = annotations
//                )

            AnnotationType.TEXT -> Unit
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.extractor_info_about_image),
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.Gray
            ),
        )
    }
}

@Composable
fun KeywordFlowRow(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    values: List<String>,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        values.forEach {
            InputChip(
                selected = false,
                onClick = { onClick(it) },
                label = { Text(it) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "clear keyword"
                    )
                }
            )
        }
    }
}

@Composable
fun VisualKeywordsEditor(
    modifier: Modifier = Modifier,
    state: EditLupaAnnotationState.VisualKeywords
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.visual_embeddings),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        KeywordFlowRow(
            values = state.keywords,
            onClick = { state.onDelete(it) }
        )
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            EditLupaAnnotationScreen(
                header = null,
                annotationType = AnnotationType.USER,
                userEditComponent = previewUserEditor
            )
        }
    }
}