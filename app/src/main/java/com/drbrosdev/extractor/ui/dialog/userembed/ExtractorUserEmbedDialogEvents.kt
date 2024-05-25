package com.drbrosdev.extractor.ui.dialog.userembed

sealed interface ExtractorUserEmbedDialogEvents {
    data object ChangesSaved: ExtractorUserEmbedDialogEvents
}