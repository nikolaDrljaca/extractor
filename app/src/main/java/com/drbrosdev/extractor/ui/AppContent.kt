package com.drbrosdev.extractor.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.domain.usecase.settings.ExtractorMainActivitySettings
import com.drbrosdev.extractor.domain.usecase.settings.ProvideMainActivitySettings
import com.drbrosdev.extractor.ui.root.Root
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import org.koin.compose.koinInject


@Composable
fun AppContent() {
    val mainSettings: ProvideMainActivitySettings = koinInject()
    val settings by
        mainSettings().collectAsStateWithLifecycle(initialValue = ExtractorMainActivitySettings())


    ExtractorTheme(dynamicColor = settings.enableDynamicTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Root()
        }
    }
}