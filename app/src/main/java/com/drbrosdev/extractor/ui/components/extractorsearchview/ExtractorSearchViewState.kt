package com.drbrosdev.extractor.ui.components.extractorsearchview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.drbrosdev.extractor.domain.model.LabelType
import kotlinx.coroutines.flow.Flow


@Stable
class ExtractorSearchViewState(
    initialQuery: String,
    initialLabelType: LabelType
) {
    var query by mutableStateOf(initialQuery)
        private set

    var labelType by mutableStateOf(initialLabelType)
        private set

    inline fun updateQuery(block: (String) -> String) {
        val new = block(query)
        updateQuery(new)
    }

    fun updateQuery(new: String) {
        query = new
    }

    fun updateLabelType(new: LabelType) {
        labelType = new
    }

    companion object {
        fun Saver() = androidx.compose.runtime.saveable.Saver<ExtractorSearchViewState, String>(
            save = { it.query },
            restore = {
                ExtractorSearchViewState(
                    initialQuery = it,
                    initialLabelType = LabelType.ALL
                )
            }
        )
    }
}

fun ExtractorSearchViewState.isBlank(): Boolean {
    return query.isBlank()
}

fun ExtractorSearchViewState.isNotBlank(): Boolean {
    return isBlank()
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
    return rememberSaveable(saver = ExtractorSearchViewState.Saver()) {
        ExtractorSearchViewState(
            initialQuery, initialLabelType
        )
    }
}

fun ExtractorSearchViewState.queryAsFlow(): Flow<String> {
    return snapshotFlow { this.query }
}

fun ExtractorSearchViewState.labelTypeAsFlow(): Flow<LabelType> {
    return snapshotFlow { this.labelType }
}