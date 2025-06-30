package com.drbrosdev.extractor.framework

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.drbrosdev.extractor.util.findActivity

class SystemBarsController(
    context: Context,
    view: View
) {
    private val insetsController = WindowInsetsControllerCompat(
        context.findActivity().window,
        view
    )

    fun show() {
        insetsController.show(WindowInsetsCompat.Type.systemBars())
    }

    fun hide() {
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

@Composable
fun rememberSystemBarsController(): SystemBarsController {
    val context = LocalContext.current
    val view = LocalView.current
    return remember { SystemBarsController(context, view) }
}
