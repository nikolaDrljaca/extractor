package com.drbrosdev.extractor.ui.components.extractorsearchview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.drbrosdev.extractor.domain.usecase.LabelType


@Stable
interface ExtractorSearchViewState {
    var query: String

    var labelType: LabelType
}

fun ExtractorSearchViewState.initialLabelTypeIndex(): Int {
    return when(labelType) {
        LabelType.ALL -> 0
        LabelType.TEXT -> 1
        LabelType.IMAGE -> 2
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

private class ExtractorSearchViewStateImpl(
    initialQuery: String,
    initialLabelType: LabelType
) : ExtractorSearchViewState {
    private val _query = mutableStateOf(initialQuery)
    private val _labelType = mutableStateOf(initialLabelType)

    override var query: String
        get() = _query.value
        set(value) {
            _query.value = value
        }

    override var labelType: LabelType
        get() = _labelType.value
        set(value) {
            _labelType.value = value
        }

    companion object {
        fun Saver() = androidx.compose.runtime.saveable.Saver<ExtractorSearchViewStateImpl, String>(
            save = { it.query },
            restore = {
                ExtractorSearchViewStateImpl(
                    initialQuery = it,
                    initialLabelType = LabelType.ALL
                )
            }
        )
    }
}
