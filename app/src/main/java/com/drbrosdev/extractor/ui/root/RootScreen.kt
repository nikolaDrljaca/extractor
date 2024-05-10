package com.drbrosdev.extractor.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Dialog
import com.drbrosdev.extractor.framework.navigation.BlankNavTarget
import com.drbrosdev.extractor.framework.navigation.BottomSheetNavTarget
import com.drbrosdev.extractor.framework.navigation.DialogNavTarget
import com.drbrosdev.extractor.framework.navigation.LocalBottomSheetNavController
import com.drbrosdev.extractor.framework.navigation.LocalDialogNavController
import com.drbrosdev.extractor.framework.navigation.LocalNavController
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.animspec.DefaultTransitionSpec
import com.drbrosdev.extractor.framework.permission.ReadPermissionAccess
import com.drbrosdev.extractor.ui.onboarding.ExtractorOnboardingNavTarget
import com.drbrosdev.extractor.ui.permhandler.ExtractorPermissionRequestNavTarget
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.DialogNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.material3.BottomSheetNavHost
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.replaceAll
import kotlinx.coroutines.flow.first
import org.koin.androidx.compose.koinViewModel


@Composable
fun Root() {
    val viewModel: RootViewModel = koinViewModel()
    val startDestination = determineStartDestination {
        viewModel.getPermissionAccessStatus()
    }

    val navController = rememberNavController(startDestination = startDestination)
    val dialogNavController = rememberNavController<DialogNavTarget>(initialBackstack = emptyList())
    val bottomSheetNavController =
        rememberNavController<BottomSheetNavTarget>(initialBackstack = emptyList())

    LaunchedEffect(key1 = Unit) {
        val isOnboardingFinished = viewModel.isOnboardingFinished().first()
        if (!isOnboardingFinished) {
            //Onboarding not shown
            navController.replaceAll(ExtractorOnboardingNavTarget)
        } else {
            //Has seen onboarding, but permission is denied
            //Probably manually revoked
            when (viewModel.getPermissionAccessStatus()) {
                ReadPermissionAccess.DENIED -> navController.replaceAll(
                    ExtractorPermissionRequestNavTarget
                )

                else -> Unit
            }
        }
    }

    NavBackHandler(controller = navController)

    NavBackHandler(controller = bottomSheetNavController, allowEmptyBackstack = true)

    AnimatedNavHost(
        controller = navController,
        transitionSpec = DefaultTransitionSpec,
    ) {
        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalDialogNavController provides dialogNavController,
            LocalBottomSheetNavController provides bottomSheetNavController
        ) {
            it.Content()
        }
    }

    DialogNavHost(controller = dialogNavController) {
        CompositionLocalProvider(LocalDialogNavController provides dialogNavController) {
            Dialog(onDismissRequest = { dialogNavController.pop() }) {
                it.Content()
            }
        }
    }

    BottomSheetNavHost(
        controller = bottomSheetNavController,
        onDismissRequest = { bottomSheetNavController.pop() }
    ) {
        CompositionLocalProvider(
            LocalBottomSheetNavController provides bottomSheetNavController,
        ) {
            it.Content(this.sheetState)
        }
    }
}

private fun determineStartDestination(
    readPermissionAccessProvider: () -> ReadPermissionAccess
): NavTarget {
    val perm = readPermissionAccessProvider()
    return when (perm) {
        ReadPermissionAccess.DENIED -> BlankNavTarget
        else -> ExtractorSearchNavTarget
    }
}