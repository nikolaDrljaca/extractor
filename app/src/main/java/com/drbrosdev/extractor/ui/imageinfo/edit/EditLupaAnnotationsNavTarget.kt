package com.drbrosdev.extractor.ui.imageinfo.edit

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
            AppImageInfoHeader(
                model = it
            )
        }

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = 12.dp,
                        vertical = 16.dp
                    )
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "you are editing"
                )
                Spacer(Modifier.width(8.dp))

                Text(text = "You are editing keywords.")
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
                annotationType = AnnotationType.USER,
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