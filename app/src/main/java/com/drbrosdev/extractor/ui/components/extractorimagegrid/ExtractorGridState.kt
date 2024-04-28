package com.drbrosdev.extractor.ui.components.extractorimagegrid

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorListItemCheckedState
import kotlinx.coroutines.flow.map

@Stable
class ExtractorGridState(
    val lazyGridState: LazyGridState = LazyGridState(),
) {
    val checkedItems = mutableStateMapOf<Int, ExtractorListItemCheckedState>()

    operator fun get(index: Int): ExtractorListItemCheckedState {
        return checkedItems.getOrDefault(index, ExtractorListItemCheckedState.UNCHECKED)
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
            .all { it == ExtractorListItemCheckedState.UNCHECKED }

        if (areAllUnchecked) {
            return false
        }

        transitionItemState(index)
        return true
    }

    fun clearSelection() {
        checkedItems.keys.forEach { index ->
            checkedItems[index] = ExtractorListItemCheckedState.UNCHECKED
        }
    }

    private fun transitionItemState(index: Int) {
        val currentState = checkedItems.getOrDefault(index, ExtractorListItemCheckedState.UNCHECKED)
        val updatedState = when (currentState) {
            ExtractorListItemCheckedState.CHECKED -> ExtractorListItemCheckedState.UNCHECKED
            ExtractorListItemCheckedState.UNCHECKED -> ExtractorListItemCheckedState.CHECKED
        }
        checkedItems[index] = updatedState
    }
}

fun ExtractorGridState.checkedIndicesAsFlow() = snapshotFlow { checkedItems.toMap() }
    .map { it.keys.filter { index -> checkedItems[index] == ExtractorListItemCheckedState.CHECKED } }

fun ExtractorGridState.checkedIndices() = checkedItems.toMap().keys
    .filter { index -> checkedItems[index] == ExtractorListItemCheckedState.CHECKED }
