package com.drbrosdev.extractor.ui.components.extractorsearchview

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.ImageLabelFilterChips
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.toLabelType
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextField
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@Composable
fun ExtractorSearchView(
    state: ExtractorSearchViewState,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = Unit) {
        /*
        TODO: @nikola Note this bug.
        Because Compose is what it is, we have to manually clear focus
        during an interaction event due to the fact that the BaseTextField is not doing it on its own.
         */
        interactionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> {
                    focusManager.clearFocus()
                    focusRequester.requestFocus()
                }
            }
        }
    }

    Surface(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.primary,
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
                textColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                interactionSource = interactionSource
            )

            ImageLabelFilterChips(
                onFilterChanged = {
                    state.updateLabelType(it.toLabelType())
                },
                contentColor = Color.White,
                initial = state.initialLabelTypeIndex()
            )
        }
    }
}



@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        ExtractorSearchView(
            onDone = {},
            state = ExtractorSearchViewState("", LabelType.ALL)
        )
    }
}
