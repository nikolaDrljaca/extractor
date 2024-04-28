package com.drbrosdev.extractor.ui.components.searchsheet


sealed interface SheetContent {
    data object SearchView : SheetContent

    data object MultiselectBar : SheetContent
}
