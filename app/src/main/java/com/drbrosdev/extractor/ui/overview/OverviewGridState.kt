package com.drbrosdev.extractor.ui.overview

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorGridState2
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedKeys

class OverviewGridState {
    val gridState: ExtractorGridState2<MediaImageId> = ExtractorGridState2()

    val multiselectState by derivedStateOf {
        gridState.checkedKeys().isNotEmpty()
    }

    val showSearchFab by derivedStateOf {
        gridState.lazyGridState.firstVisibleItemIndex > 0
    }

    fun onToggleCheckedItem(entry: MediaImageId): Boolean {
        return gridState.onItemClick(entry).not()
    }

    fun onLongTap(entry: MediaImageId) {
        gridState.onItemLongClick(entry)
    }
}