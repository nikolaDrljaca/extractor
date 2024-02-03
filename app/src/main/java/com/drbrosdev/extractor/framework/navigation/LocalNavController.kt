package com.drbrosdev.extractor.framework.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import dev.olshevski.navigation.reimagined.NavController



val LocalNavController = staticCompositionLocalOf<NavController<NavTarget>> {
    error("NavController was not instantiated, or is unavailable in this context.")
}

val LocalDialogNavController = staticCompositionLocalOf<NavController<DialogNavTarget>> {
    error("DialogNavController was not instantiated, or is unavailable in this context.")
}

val LocalBottomSheetNavController = staticCompositionLocalOf<NavController<BottomSheetNavTarget>> {
    error("BottomSheetNavController was not instantiated, or is unavailable in this scope.")
}
