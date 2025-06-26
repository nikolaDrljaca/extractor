package com.drbrosdev.extractor.ui.imageinfo.edit

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.AnnotationType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.repository.LupaImageRepository
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.shared.AppImageInfoHeader
import com.drbrosdev.extractor.ui.imageinfo.LupaImageHeaderState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.WhileUiSubscribed
import com.drbrosdev.extractor.util.asFormatDate
import com.drbrosdev.extractor.util.isScrollingUp
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Parcelize
data class EditLupaAnnotationsNavTarget(
    val mediaImageId: Long,
    val type: AnnotationType
) : NavTarget {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: EditLupaAnnotationsViewModel = koinViewModel {
            parametersOf(mediaImageId, type)
        }

        val annotationState by viewModel.annotationsState.collectAsStateWithLifecycle()
        val headerState by viewModel.headerState.collectAsStateWithLifecycle()
        val loading by viewModel.loading.collectAsStateWithLifecycle()

        when {
            loading.not() -> EditLupaAnnotationsScreen(
                modifier = Modifier.fillMaxSize(),
                annotations = annotationState!!,
                headerState = headerState!!,
            )

            else -> Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                LoadingIndicator()
            }
        }
    }
}

class EditLupaAnnotationsViewModel(
    private val mediaImageId: Long,
    private val annotationType: AnnotationType,
    private val lupaImageRepository: LupaImageRepository
) : ViewModel() {

    val headerState = lupaImageRepository.findByIdAsFlow(MediaImageId(mediaImageId))
        .filterNotNull()
        .map { lupaImage ->
            LupaImageHeaderState(
                mediaImageId = lupaImage.metadata.mediaImageId.id,
                uri = lupaImage.metadata.uri.uri,
                dateAdded = lupaImage.metadata.dateAdded.asFormatDate()
            )
        }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            null
        )

    val annotationsState = lupaImageRepository.findImageDataByMediaId(MediaImageId(mediaImageId))
        .filterNotNull()
        .map {
            when (annotationType) {
                AnnotationType.TEXT, AnnotationType.VISUAL -> it.visualEmbeds
                AnnotationType.USER -> it.userEmbeds
            }
        }
        .map { AnnotationListState(it, annotationType, {}) }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            null
        )

    val loading = combine(headerState, annotationsState) { header, annotations ->
        header == null && annotations == null
    }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            true
        )
}


@Immutable
data class AnnotationListState(
    val items: List<String>,
    val type: AnnotationType,
    val onDelete: (annotationValue: String) -> Unit
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditLupaAnnotationsScreen(
    modifier: Modifier = Modifier,
    headerState: LupaImageHeaderState,
    annotations: AnnotationListState
) {
    val listState = rememberLazyListState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item(key = "header") {
                AppImageInfoHeader(
                    model = headerState,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            item {
                Surface(
                    shape = MaterialTheme.shapes.largeIncreased,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    val icon = when (annotations.type) {
                        AnnotationType.TEXT -> R.drawable.round_text_fields_24
                        AnnotationType.VISUAL -> R.drawable.round_image_search_24
                        AnnotationType.USER -> R.drawable.baseline_android_24
                    }
                    val text = when (annotations.type) {
                        AnnotationType.TEXT -> "Text keywords"
                        AnnotationType.VISUAL -> "Visual keywords"
                        AnnotationType.USER -> "My keywords"
                    }
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "resource",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(text = text)
                    }
                }
            }

            // for both visual and user annotations
            itemsIndexed(annotations.items) { index, item ->
                val position = remember {
                    when (index) {
                        0 -> ItemPosition.FIRST
                        annotations.items.lastIndex -> ItemPosition.LAST
                        else -> ItemPosition.MIDDLE
                    }
                }
                AnnotationListItem(
                    value = item,
                    position = position,
                    onDelete = annotations.onDelete
                )
            }
        }

        AnimatedVisibility(
            visible = listState.isScrollingUp(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 15,
                    delayMillis = 30,
                    easing = LinearEasing,
                ),
            ),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = 15,
                    delayMillis = 150,
                    easing = LinearEasing,
                )
            )
        ) {
            ExtendedFloatingActionButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "add"
                )
                Text(
                    text = "Add"
                )
            }
        }
    }
}

enum class ItemPosition {
    FIRST, MIDDLE, LAST
}

@Composable
fun AnnotationListItem(
    modifier: Modifier = Modifier,
    value: String,
    position: ItemPosition,
    onDelete: (annotationValue: String) -> Unit
) {
    // TODO Performance?
    val shape = remember(position) {
        when (position) {
            ItemPosition.FIRST -> RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = 2.dp,
                bottomEnd = 2.dp
            )

            ItemPosition.LAST -> RoundedCornerShape(
                topStart = 2.dp,
                topEnd = 2.dp,
                bottomStart = 12.dp,
                bottomEnd = 12.dp
            )

            ItemPosition.MIDDLE -> RoundedCornerShape(2.dp)
        }
    }
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> onDelete(value)
                SwipeToDismissBoxValue.EndToStart -> onDelete(value)
                SwipeToDismissBoxValue.Settled -> Unit
            }
            it != SwipeToDismissBoxValue.StartToEnd
        }
    )
    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = modifier,
        backgroundContent = {
            when (swipeToDismissBoxState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "delete",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.error)
                            .wrapContentSize(Alignment.CenterStart)
                            .padding(12.dp)
                            .clip(shape),
                        tint = MaterialTheme.colorScheme.onError
                    )
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "delete",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.error)
                            .wrapContentSize(Alignment.CenterEnd)
                            .padding(12.dp)
                            .clip(shape),
                        tint = MaterialTheme.colorScheme.onError
                    )
                }

                SwipeToDismissBoxValue.Settled -> Unit
            }
        }
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = value,
                )
            },
            modifier = Modifier
                .padding(bottom = 2.dp)
                .clip(shape),
        )
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            EditLupaAnnotationsScreen(
                headerState = LupaImageHeaderState(
                    mediaImageId = 12123123,
                    uri = Uri.EMPTY.toString(),
                    dateAdded = "2025-01-01"
                ),
                annotations = AnnotationListState(
                    items = buildList {
                        repeat(12) { add("Item $it") }
                    },
                    onDelete = {},
                    type = AnnotationType.VISUAL
                )
            )
        }
    }
}