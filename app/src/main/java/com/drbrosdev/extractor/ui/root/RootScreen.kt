package com.drbrosdev.extractor.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.googlefonts.isAvailableOnDevice
import androidx.compose.ui.window.Dialog
import com.drbrosdev.extractor.framework.logger.logEvent
import com.drbrosdev.extractor.framework.navigation.BlankNavTarget
import com.drbrosdev.extractor.framework.navigation.BottomSheetNavTarget
import com.drbrosdev.extractor.framework.navigation.DialogNavTarget
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.framework.navigation.animspec.createTransitionSpec
import com.drbrosdev.extractor.framework.permission.ReadPermissionAccess
import com.drbrosdev.extractor.ui.onboarding.AppOnboardingNavTarget
import com.drbrosdev.extractor.ui.overview.ExtractorOverviewNavTarget
import com.drbrosdev.extractor.ui.permhandler.ExtractorPermissionRequestNavTarget
import com.drbrosdev.extractor.ui.theme.provider
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

    val navigators = remember {
        Navigators(navController, dialogNavController, bottomSheetNavController)
    }

    val density = LocalDensity.current
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        val isOnboardingFinished = viewModel.isOnboardingFinished().first()
        if (!isOnboardingFinished) {
            //Onboarding not shown
            navController.replaceAll(AppOnboardingNavTarget)
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

    LaunchedEffect(Unit) {
        if (provider.isAvailableOnDevice(context)) {
            logEvent("Downloadable fonts are available.")
        }
    }

    NavBackHandler(controller = navController)

    NavBackHandler(controller = bottomSheetNavController, allowEmptyBackstack = true)

    AnimatedNavHost(
        controller = navController,
        transitionSpec = createTransitionSpec(density),
    ) {
        it.Content(navigators)
    }

    DialogNavHost(controller = dialogNavController) {
        Dialog(onDismissRequest = { dialogNavController.pop() }) {
            it.Content(dialogNavController)
        }
    }

    BottomSheetNavHost(
        controller = bottomSheetNavController,
        onDismissRequest = { bottomSheetNavController.pop() }
    ) {
        it.Content(
            sheetState = sheetState,
            dialogNavController = dialogNavController,
            sheetNavController = bottomSheetNavController
        )
    }
}

private fun determineStartDestination(
    readPermissionAccessProvider: () -> ReadPermissionAccess
): NavTarget {
    val perm = readPermissionAccessProvider()
    return when (perm) {
        ReadPermissionAccess.DENIED -> BlankNavTarget
        else -> ExtractorOverviewNavTarget
    }
}