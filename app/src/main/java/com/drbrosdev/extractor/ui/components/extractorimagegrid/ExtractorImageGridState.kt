package com.drbrosdev.extractor.ui.components.extractorimagegrid

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorImageItemState
import kotlinx.coroutines.flow.map

class ExtractorImageGridState(
    val lazyGridState: LazyGridState = LazyGridState(),
) {
    private val _checkedItems = mutableStateMapOf<Int, ExtractorImageItemState>()

    val checkedItems = _checkedItems.toMap()

    operator fun get(index: Int): ExtractorImageItemState {
        return _checkedItems.getOrDefault(index, ExtractorImageItemState.UNCHECKED)
    }

    fun onItemLongClick(index: Int) {
        transitionItemState(index)
    }

    /**
     * Handles the state transition of the item in the checked items map.
     * Use to propagate click events if necessary.
     *
     * @return If the state transition for the item at index was handled.
     */
    fun onItemClick(index: Int): Boolean {
        val areAllUnchecked = _checkedItems.values
            .all { it == ExtractorImageItemState.UNCHECKED }

        if (areAllUnchecked) {
            return false
        }

        transitionItemState(index)
        return true
    }

    private fun transitionItemState(index: Int) {
        val currentState = _checkedItems.getOrDefault(index, ExtractorImageItemState.UNCHECKED)
        val updatedState = when (currentState) {
            ExtractorImageItemState.CHECKED -> ExtractorImageItemState.UNCHECKED
            ExtractorImageItemState.UNCHECKED -> ExtractorImageItemState.CHECKED
        }
        _checkedItems[index] = updatedState
    }
}

fun ExtractorImageGridState.checkedIndicesAsFlow() = snapshotFlow { checkedItems.toMap() }
    .map { it.keys.filter { index -> checkedItems[index] == ExtractorImageItemState.CHECKED } }