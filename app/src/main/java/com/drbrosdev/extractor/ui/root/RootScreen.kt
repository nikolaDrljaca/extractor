package com.drbrosdev.extractor.ui.root

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import com.drbrosdev.extractor.ui.onboarding.Onboarding
import com.drbrosdev.extractor.ui.permhandler.ExtractorPermissionRequestNavTarget
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import com.drbrosdev.extractor.util.BottomSheetNavTarget
import com.drbrosdev.extractor.util.DefaultTransitionSpec
import com.drbrosdev.extractor.util.DialogNavTarget
import com.drbrosdev.extractor.util.LocalBottomSheetNavController
import com.drbrosdev.extractor.util.LocalDialogNavController
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
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
    val context = LocalContext.current

    val navController =
        rememberNavController<NavTarget>(startDestination = ExtractorSearchNavTarget())
    val dialogNavController = rememberNavController<DialogNavTarget>(initialBackstack = emptyList())
    val bottomSheetNavController =
        rememberNavController<BottomSheetNavTarget>(initialBackstack = emptyList())

    LaunchedEffect(key1 = Unit) {
        val isOnboardingFinished = viewModel.isOnboardingFinished().first()
        if (!isOnboardingFinished) {
            //Onboarding not shown
            navController.replaceAll(Onboarding)
        } else {
            //Has seen onboarding, but permission is denied
            //Probably manually revoked
            val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            when (context.checkSelfPermission(perm)) {
                PackageManager.PERMISSION_DENIED -> navController.replaceAll(ExtractorPermissionRequestNavTarget)
                PackageManager.PERMISSION_GRANTED -> Unit
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

