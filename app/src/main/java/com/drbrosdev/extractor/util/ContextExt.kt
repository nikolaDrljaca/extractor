package com.drbrosdev.extractor.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.MediaStoreImage
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
        Uri.parse(getString(R.string.app_store_link))
    )
    startActivity(intent)
}

fun Context.launchViewIntent(link: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    startActivity(intent)
}

fun Context.launchWebpageIntent() {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.webage)))
    startActivity(intent)
}

fun Context.launchPrivacyPolicyIntent() {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.webpage_privacy)))
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
        data = Uri.parse("mailto:") // Only email apps handle this.
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
