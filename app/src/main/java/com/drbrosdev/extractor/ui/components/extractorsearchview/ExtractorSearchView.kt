package com.drbrosdev.extractor.ui.components.extractorsearchview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.usecase.LabelType
import com.drbrosdev.extractor.ui.components.datafilterchip.ImageLabelFilterChipData
import com.drbrosdev.extractor.ui.components.datafilterchip.ImageLabelFilterChips
import com.drbrosdev.extractor.ui.components.datafilterchip.toLabelType
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextField
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Stable
interface ExtractorSearchViewState {
    val query: State<String>
    fun onQueryChange(value: String)


    var labelType: LabelType
}

private class ExtractorSearchViewStateImpl(
    initialQuery: String,
    initialLabelType: LabelType
) : ExtractorSearchViewState {
    private var _query = mutableStateOf(initialQuery)
    private var _labelType = mutableStateOf(initialLabelType)

    override val query: State<String>
        get() = _query

    override var labelType: LabelType
        get() = _labelType.value
        set(value) {
            _labelType.value = value
        }

    override fun onQueryChange(value: String) {
        _query.value = value
    }

    companion object {
        fun Saver() = Saver<ExtractorSearchViewStateImpl, String>(
            save = { it.query.value },
            restore = {
                ExtractorSearchViewStateImpl(
                    initialQuery = it,
                    initialLabelType = LabelType.ALL
                )
            }
        )
    }
}

@Composable
fun rememberExtractorSearchViewState(
    initialQuery: String,
    initialLabelType: LabelType
): ExtractorSearchViewState {
    return rememberSaveable(saver = ExtractorSearchViewStateImpl.Saver()) {
        ExtractorSearchViewStateImpl(
            initialQuery, initialLabelType
        )
    }
}

fun ExtractorSearchViewState(
    initialQuery: String,
    initialLabelType: LabelType
): ExtractorSearchViewState = ExtractorSearchViewStateImpl(
    initialQuery,
    initialLabelType
)


@Composable
fun ExtractorSearchView(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    state: ExtractorSearchViewState,
) {
    Surface(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.primary,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ExtractorTextField(
                text = state.query.value,
                onChange = state::onQueryChange,
                onDoneSubmit = onDone,
                textColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            ImageLabelFilterChips(
                onFilterChanged = {
                    state.labelType = it.toLabelType()
                },
                contentColor = Color.White
            )
        }
    }
}

@Composable
fun ExtractorSearchView(
    onDone: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onFilterChanged: (ImageLabelFilterChipData) -> Unit,
    modifier: Modifier = Modifier,
    initialQuery: String = "",
) {
    val (text, setText) = rememberSaveable {
        mutableStateOf(initialQuery)
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ExtractorTextField(
                text = text,
                onChange = {
                    setText(it)
                    onQueryChanged(it)
                },
                onDoneSubmit = onDone,
                textColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            ImageLabelFilterChips(
                onFilterChanged = onFilterChanged,
                contentColor = Color.White
            )
        }
    }
}


@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        ExtractorSearchView(onDone = {}, onFilterChanged = {}, onQueryChanged = {})
    }
}
