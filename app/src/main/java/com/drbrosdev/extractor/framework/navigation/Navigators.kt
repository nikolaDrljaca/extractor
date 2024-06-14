package com.drbrosdev.extractor.framework.navigation

import dev.olshevski.navigation.reimagined.NavController

data class Navigators(
    val navController: NavController<NavTarget>,
    val dialogNavController: NavController<DialogNavTarget>,
    val bottomSheetNavController: NavController<BottomSheetNavTarget>
)