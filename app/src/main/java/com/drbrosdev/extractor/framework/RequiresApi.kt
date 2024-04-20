package com.drbrosdev.extractor.framework

import android.os.Build
import com.drbrosdev.extractor.BuildConfig

object VersionCodes {
    const val V_21 = Build.VERSION_CODES.LOLLIPOP
    const val V_23 = Build.VERSION_CODES.M
    const val V_26 = Build.VERSION_CODES.O
    const val V_28 = Build.VERSION_CODES.P
    const val V_29 = Build.VERSION_CODES.Q
    const val V_30 = Build.VERSION_CODES.R
    const val V_31 = Build.VERSION_CODES.S
    const val V_32 = Build.VERSION_CODES.TIRAMISU
    const val V_34 = Build.VERSION_CODES.UPSIDE_DOWN_CAKE
}


fun requiresApi(
    apiLevel: Int,
    fallback: (() -> Unit)? = null,
    block: () -> Unit,
) {
    if (Build.VERSION.SDK_INT >= apiLevel) {
        block()
    } else {
        fallback?.invoke()
    }
}

fun <T> requiresApi(
    versionCode: Int,
    fallback: (() -> T),
    block: () -> T,
): T {
    return if (Build.VERSION.SDK_INT >= versionCode) {
        block()
    } else {
        fallback.invoke()
    }
}

fun requireDebug(
    fallback: (() -> Unit)? = null,
    action: () -> Unit
) {
    if (BuildConfig.DEBUG) {
        action()
    } else {
        fallback?.invoke()
    }
}