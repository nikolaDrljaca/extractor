package com.drbrosdev.extractor.framework.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat

typealias PermissionRequest = () -> Unit

class PermissionService(
    private val context: Context
) {

    fun getReadPermissionAccessStatus(): ReadPermissionAccess {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED) -> {
                // Full access on Android 13 (API level 33) or higher
                ReadPermissionAccess.FULL
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    ) == PackageManager.PERMISSION_GRANTED -> {
                // Partial access on Android 14 (API level 34) or higher
                ReadPermissionAccess.PARTIAL
            }

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Full access up to Android 12 (API level 32)
                ReadPermissionAccess.FULL
            }

            else -> ReadPermissionAccess.DENIED
        }
    }

    companion object {

        /**
         * Starts a request for the proper permissions to gain access to device images.
         * Method will determine the appropriate permissions based on device API level.
         *
         * Example usage:
         * ```kotlin
         * val request = PermissionService.requestPermissions { isGranted ->
         *      processResult(isGranted)
         * }
         *
         * request.invoke()
         * ```
         *
         * @param [onResult] callback that will provide the result of the request.
         * @return [PermissionRequest] function to invoke to start the permission request.
         */
        @Composable
        fun requestPermissions(
            onResult: (isGranted: Boolean) -> Unit
        ): PermissionRequest {
            val out =
                rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    val isGranted = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                            val readMediaImages = it[Manifest.permission.READ_MEDIA_IMAGES] ?: false
                            val readUserSelectedImages =
                                it[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] ?: false
                            readMediaImages or readUserSelectedImages
                        }

                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                            it[Manifest.permission.READ_MEDIA_IMAGES] ?: false
                        }

                        else -> {
                            it[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
                        }
                    }
                    onResult(isGranted)
                }
            // Permission request logic
            val perms = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    )
                }

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                }

                else -> {
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

            return { out.launch(perms) }
        }
    }
}