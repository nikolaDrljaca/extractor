package com.drbrosdev.extractor.ui.components.extractorsearchview

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.KeywordTypeChips
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.toChipDataIndex
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.toKeywordType
import com.drbrosdev.extractor.ui.components.shared.ExtractorSearchTextField
import com.drbrosdev.extractor.ui.components.shared.SearchTypeSwitch
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.KeyboardState
import com.drbrosdev.extractor.util.rememberKeyboardState


@Composable
fun ExtractorSearchView(
    state: ExtractorSearchViewState,
    onDone: () -> Unit,
    onKeywordTypeChange: () -> Unit,
    onSearchTypeChange: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
) {
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

    Column(
        modifier = Modifier
            .then(modifier)
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ExtractorSearchTextField(
            text = state.query,
            onChange = state::updateQuery,
            onDoneSubmit = onDone,
            textColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            interactionSource = interactionSource,
            enabled = !state.disabled
        )

        Column {
            KeywordTypeChips(
                onFilterChanged = {
                    val keywordType = it.toKeywordType()
                    state.updateKeywordType(keywordType)
                    onKeywordTypeChange()
                },
                selection = state.keywordType.toChipDataIndex(),
                enabled = !state.disabled
            )

            SearchTypeSwitch(
                selection = state.searchType,
                onSelectionChanged = {
                    state.updateSearchType(it)
                    onSearchTypeChange()
                },
                enabled = !state.disabled
            )
        }
    }
}


@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            ExtractorSearchView(
                onDone = {},
                state = ExtractorSearchViewState(
                    "",
                    KeywordType.ALL,
                    SearchType.PARTIAL,
                    initialIsDisabled = true,
                ),
                onKeywordTypeChange = {},
                onSearchTypeChange = {}
            )
        }
    }
}
