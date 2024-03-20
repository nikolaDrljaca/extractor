package com.drbrosdev.extractor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.usecase.settings.ProvideMainActivitySettings
import com.drbrosdev.extractor.ui.root.Root
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.framework.logger.logInfo
import com.drbrosdev.extractor.util.setupInitialThemeLoad
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setupInitialThemeLoad { viewModel.isDynamicTheme.value }

        logInfo("Starting")

        setContent {
            viewModel.isDynamicTheme
                .collectAsStateWithLifecycle()
                .value?.let {
                    ExtractorTheme(dynamicColor = it) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Root()
                        }
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (application as ExtractorApplication).databaseLogger.close()
    }
}

class MainViewModel(
    private val mainSettings: ProvideMainActivitySettings
) : ViewModel() {
    val isDynamicTheme = mainSettings()
        .map { it.enableDynamicTheme }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )
}