package com.drbrosdev.extractor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.koin.androidx.compose.koinViewModel


@Composable
fun SearchScreen(
    onOpenAppSettings: () -> Unit,
    onNavigateToPager: () -> Unit
) {
    val viewModel = koinViewModel<MainViewModel>()
    val isRunning by viewModel.isWorkRunning.collectAsState(initial = false)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val images by viewModel.images.collectAsState()
        val (text, setText) = remember {
            mutableStateOf("")
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
                    Text(text = "Run Worker", style = MaterialTheme.typography.headlineMedium)
                }
            }
            item {
                if (isRunning) CircularProgressIndicator()
            }
            items(images) {
                Column(
                    modifier = Modifier.clickable { }
                ) {
                    Text(text = it.id.toString())
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
    }
}
