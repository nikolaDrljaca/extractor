package com.drbrosdev.extractor

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onOpenAppSettings: () -> Unit,
    onNavigateToPager: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = koinViewModel<MainViewModel>()
    val extractor = Extractor(context = context)
    val persistentExtractor = PersistentExtractor(extractor, context.contentResolver)

    val imagePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                viewModel.onPermissionResult(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    isGranted = isGranted
                )
            } else {
                viewModel.onPermissionResult(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    isGranted = isGranted
                )
            }
        }
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (viewModel.permissionGranted.value) {
            val images by viewModel.images.collectAsState()
            val (text, setText) = remember {
                mutableStateOf("")
            }

            LaunchedEffect(key1 = Unit) {
//                viewModel.runExtraction(persistentExtractor)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    6.dp,
                    alignment = Alignment.CenterVertically
                ),
            ) {
                item {
                    Row {
                        OutlinedTextField(
                            value = text,
                            onValueChange = setText,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = { viewModel.performSearch(text) }) {
                            Text(text = "Search")
                        }
                    }
                }
                item {
                    Button(onClick = onNavigateToPager) {
                        Text(text = "Navigate to pager")
                    }
                }

                item {
                    Button(onClick = { viewModel.spawnWorkRequest() }) {
                        Text(text = "Run Worker")
                    }
                }
                items(images) {
                    Column(
                        modifier = Modifier.clickable {
                            viewModel.runExtraction(it.uri, extractor)
                        }
                    ) {
                        Text(text = it.mediaStoreId.toString())
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it.uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = null
                        )
                    }
                }
            }
        } else {
            RequestPermissionScreen(
                onRequestPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        imagePermissionResultLauncher.launch(
                            Manifest.permission.READ_MEDIA_IMAGES
                        )
                    } else {
                        imagePermissionResultLauncher.launch(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                },
                onOpenAppSettings = onOpenAppSettings
            )
        }
    }
}
