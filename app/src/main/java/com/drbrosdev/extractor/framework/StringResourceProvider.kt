package com.drbrosdev.extractor.framework

import android.content.Context
import androidx.annotation.StringRes

/**
 * Component that allows access to string resources by hiding the Context to prevent leakage.
 * Commonly needed in ViewModels or other non-ui code to get i18n values for strings.
 *
 * Use sparingly. String resources should not be needed outside UI code where Context is available.
 *
 *
 * **HAVE A GOOD REASON TO USE THIS AND STATE IN COMMENT WHY!**
 */
class StringResourceProvider(
    private val context: Context
) {

    fun get(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    fun get(@StringRes resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, formatArgs)
    }

}