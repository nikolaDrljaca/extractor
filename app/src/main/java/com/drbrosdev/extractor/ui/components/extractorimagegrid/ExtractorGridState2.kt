package com.drbrosdev.extractor.ui.components.extractorimagegrid

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorListItemCheckedState
import kotlinx.coroutines.flow.map

// TODO Make the main
@Stable
class ExtractorGridState2<T>(
    val lazyGridState: LazyGridState = LazyGridState(),
) {
    val checkedItems = mutableStateMapOf<T, ExtractorListItemCheckedState>()

    operator fun get(key: T): ExtractorListItemCheckedState {
        return checkedItems.getOrDefault(key, ExtractorListItemCheckedState.UNCHECKED)
    }

    fun onItemLongClick(key: T) {
        transitionItemState(key)
    }

    /**
     * Handles the state transition of the item in the checked items map.
     * Use to propagate click events if necessary.
     *
     * @return If the state transition for the item at index was handled.
     */
    fun onItemClick(key: T): Boolean {
        val areAllUnchecked = checkedItems.values
            .all { it == ExtractorListItemCheckedState.UNCHECKED }

        if (areAllUnchecked) {
            return false
        }

        transitionItemState(key)
        return true
    }

    fun clearSelection() {
        checkedItems.keys.forEach { key ->
            checkedItems[key] = ExtractorListItemCheckedState.UNCHECKED
        }
    }

    private fun transitionItemState(key: T) {
        val currentState = checkedItems.getOrDefault(key, ExtractorListItemCheckedState.UNCHECKED)
        val updatedState = when (currentState) {
            ExtractorListItemCheckedState.CHECKED -> ExtractorListItemCheckedState.UNCHECKED
            ExtractorListItemCheckedState.UNCHECKED -> ExtractorListItemCheckedState.CHECKED
        }
        checkedItems[key] = updatedState
    }
}

fun <T> ExtractorGridState2<T>.checkedKeysAsFlow() = snapshotFlow { checkedItems.toMap() }
    .map { it.keys.filter { index -> checkedItems[index] == ExtractorListItemCheckedState.CHECKED } }

fun <T> ExtractorGridState2<T>.checkedKeys() = checkedItems.toMap().keys
    .filter { index -> checkedItems[index] == ExtractorListItemCheckedState.CHECKED }
