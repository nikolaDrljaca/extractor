package com.drbrosdev.extractor.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.MediaStoreImage
import com.drbrosdev.extractor.framework.logger.logEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Context.launchShareIntent(media: MediaStoreImage) =
    withContext(Dispatchers.Default) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = media.mimeType
            putExtra(Intent.EXTRA_STREAM, media.uri)
            putExtra(Intent.EXTRA_SUBJECT, media.displayName)
        }
        startActivity(Intent.createChooser(intent, "Share Image via..."))
    }

fun Context.launchShareIntent(mediaImages: List<Uri>) {
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "image/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(mediaImages))
    }
    startActivity(Intent.createChooser(intent, "Share Images via..."))
}

fun Context.launchShareIntent(content: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, content)
    }
    startActivity(Intent.createChooser(intent, null))
}

fun Context.launchTranslateIntent(content: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, content)
        putExtra("key_text_input", content)
        putExtra("key_text_output", "")
        putExtra("key_language_from", "en")
        putExtra("key_language_to", "mal")
        putExtra("key_suggest_translation", "")
        putExtra("key_from_floating_window", false)
        component = ComponentName(
            "com.google.android.apps.translate",
            "com.google.android.apps.translate.TranslateActivity"
        )
    }
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        logEvent("Google translate not found on device. Not possible to launch intent.")
    }
}

fun Context.launchShareAppIntent() {
    val content = buildString {
        appendLine("Checkout this Lupa on the Play Store!")
        appendLine(getString(R.string.play_store_link))
    }
    launchShareIntent(content)
}

fun Context.launchPlayStorePage() {
    val intent = Intent(
        Intent.ACTION_VIEW,
        getString(R.string.play_store_link).toUri()
    )
    startActivity(intent)
}

fun Context.launchViewIntent(link: String) {
    val intent = Intent(Intent.ACTION_VIEW, link.toUri())
    startActivity(intent)
}

fun Context.launchWebpageIntent() {
    val intent = Intent(Intent.ACTION_VIEW, getString(R.string.webage).toUri())
    startActivity(intent)
}

fun Context.launchPrivacyPolicyIntent() {
    val intent = Intent(Intent.ACTION_VIEW, getString(R.string.webpage_privacy).toUri())
    startActivity(intent)
}

suspend fun Context.launchEditIntent(media: MediaStoreImage) =
    withContext(Dispatchers.Default) {
        val intent = Intent(Intent.ACTION_EDIT).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            setDataAndType(media.uri, media.mimeType)
            putExtra("mimeType", media.mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.edit)))
    }

suspend fun Context.launchUseAsIntent(media: MediaStoreImage) =
    withContext(Dispatchers.Default) {
        val intent = Intent(Intent.ACTION_ATTACH_DATA).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            setDataAndType(media.uri, media.mimeType)
            putExtra("mimeType", media.mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.set_as)))
    }


fun Context.launchEmailIntent(content: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri() // Only email apps handle this.
        putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
        putExtra(Intent.EXTRA_SUBJECT, "Extractor Feedback Submission")
        putExtra(Intent.EXTRA_TEXT, content)
    }

    startActivity(intent)
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    panic("No activity found.")
}

fun Context.checkAndRequestPermission(
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val result = ContextCompat.checkSelfPermission(this, permission)
    if (result != PackageManager.PERMISSION_GRANTED) {
        launcher.launch(permission)
    }
}

fun Context.showToast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showToast(messageResId: Int) {
    val message = getString(messageResId)
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
