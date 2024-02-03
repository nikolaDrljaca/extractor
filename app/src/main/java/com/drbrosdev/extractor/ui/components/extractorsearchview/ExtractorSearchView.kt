package com.drbrosdev.extractor.ui.components.extractorsearchview

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.ImageLabelFilterChips
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.toLabelType
import com.drbrosdev.extractor.ui.components.shared.ExtractorSearchTypeSwitch
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextField
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.KeyboardState
import com.drbrosdev.extractor.util.rememberKeyboardState


@Composable
fun ExtractorSearchView(
    state: ExtractorSearchViewState,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    isHidden: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    textFieldPadding: PaddingValues = PaddingValues()
) {
    val alphaOffset by animateFloatAsState(targetValue = if (isHidden) 0f else 1f, label = "")
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current
    val keyboardState = rememberKeyboardState()

    SideEffect {
        /*
        TODO: @nikola Note this bug.
        Because Compose is what it is, textFields are not dropping focus automatically thus
        not allowing the keyboard to show up AFTER the field was focused for the first time and the
        keyboard was dismissed.
        We listen to the show/hide state of the keyboard, and if its hidden we FORCE clear focus
         */
        if (keyboardState.value == KeyboardState.HIDDEN) {
            focusManager.clearFocus()
        }
    }

    Surface(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ExtractorTextField(
                text = state.query,
                onChange = state::updateQuery,
                onDoneSubmit = onDone,
                textColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(textFieldPadding)
                    .fillMaxWidth(),
                interactionSource = interactionSource
            )

            Column(
                modifier = Modifier.graphicsLayer {
                    alpha = alphaOffset
                }
            ) {
                ImageLabelFilterChips(
                    onFilterChanged = {
                        state.updateKeywordType(it.toLabelType())
                    },
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    initial = state.initialLabelTypeIndex(),
                )

                ExtractorSearchTypeSwitch(
                    selection = state.searchType,
                    onSelectionChanged = state::updateSearchType,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}


@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorSearchView(
            onDone = {},
            state = ExtractorSearchViewState(
                "",
                KeywordType.ALL,
                SearchType.PARTIAL
            )
        )
    }
}
