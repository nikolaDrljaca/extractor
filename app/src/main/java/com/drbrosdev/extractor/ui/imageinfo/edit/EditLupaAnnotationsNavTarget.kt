package com.drbrosdev.extractor.ui.imageinfo.edit

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.AnnotationType
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.shared.AppImageInfoHeader
import com.drbrosdev.extractor.ui.imageinfo.LupaImageHeaderState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
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
        val visualAnnotations by viewModel.visualAnnotationState.collectAsStateWithLifecycle()

        EditLupaAnnotationScreen(
            header = header,
            annotationType = type,
            userEditComponent = viewModel.userEditComponent,
            visualAnnotationsState = visualAnnotations,
            textAnnotationState = viewModel.textAnnotationState
        )
    }
}

@Composable
fun EditLupaAnnotationScreen(
    header: LupaImageHeaderState?,
    annotationType: AnnotationType,
    userEditComponent: UserAnnotationsEditorComponent,
    visualAnnotationsState: VisualAnnotationsState,
    textAnnotationState: TextFieldState
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
            .systemBarsPadding()
    ) {
        header?.let {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "you are editing",
                        modifier = Modifier.align(Alignment.TopEnd),
                    )

                    AppImageInfoHeader(
                        model = it,
                    )
                }
            }
        }

        when (annotationType) {
            AnnotationType.USER -> UserAnnotationsEditor(
                component = userEditComponent
            )

            AnnotationType.VISUAL ->
                VisualKeywordsEditor(
                    state = visualAnnotationsState
                )

            AnnotationType.TEXT -> TextAnnotationEditor(
                text = textAnnotationState
            )
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

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            EditLupaAnnotationScreen(
                header = LupaImageHeaderState(
                    mediaImageId = 12123123,
                    uri = Uri.EMPTY.toString(),
                    dateAdded = "2025-01-01"
                ),
                annotationType = AnnotationType.TEXT,
                userEditComponent = previewUserEditor,
                visualAnnotationsState = VisualAnnotationsState(
                    keywords = emptyList(),
                    onDelete = {}
                ),
                textAnnotationState = TextFieldState("")
            )
        }
    }
}