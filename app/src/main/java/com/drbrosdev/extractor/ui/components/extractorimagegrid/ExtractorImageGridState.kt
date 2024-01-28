package com.drbrosdev.extractor.ui.components.extractorimagegrid

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorImageItemState
import kotlinx.coroutines.flow.map

class ExtractorImageGridState(
    val lazyGridState: LazyGridState = LazyGridState(),
) {
    val checkedItems = mutableStateMapOf<Int, ExtractorImageItemState>()

    operator fun get(index: Int): ExtractorImageItemState {
        return checkedItems.getOrDefault(index, ExtractorImageItemState.UNCHECKED)
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
        val areAllUnchecked = checkedItems.values
            .all { it == ExtractorImageItemState.UNCHECKED }

        if (areAllUnchecked) {
            return false
        }

        transitionItemState(index)
        return true
    }

    fun clearSelection() {
        checkedItems.keys.forEach { index ->
            checkedItems[index] = ExtractorImageItemState.UNCHECKED
        }
    }

    private fun transitionItemState(index: Int) {
        val currentState = checkedItems.getOrDefault(index, ExtractorImageItemState.UNCHECKED)
        val updatedState = when (currentState) {
            ExtractorImageItemState.CHECKED -> ExtractorImageItemState.UNCHECKED
            ExtractorImageItemState.UNCHECKED -> ExtractorImageItemState.CHECKED
        }
        checkedItems[index] = updatedState
    }
}

fun ExtractorImageGridState.checkedIndicesAsFlow() = snapshotFlow { checkedItems.toMap() }
    .map { it.keys.filter { index -> checkedItems[index] == ExtractorImageItemState.CHECKED } }

fun ExtractorImageGridState.checkedIndices() = checkedItems.toMap().keys
    .filter { index -> checkedItems[index] == ExtractorImageItemState.CHECKED }
