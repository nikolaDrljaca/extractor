package com.drbrosdev.extractor.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.BuildConfig
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.actionchips.AboutLink
import com.drbrosdev.extractor.ui.components.actionchips.ExtractorActionChips
import com.drbrosdev.extractor.ui.components.extractorabout.ExtractorAbout
import com.drbrosdev.extractor.ui.components.extractorlicenses.ExtractorLicenses
import com.drbrosdev.extractor.ui.components.extractorsettings.ExtractorRedZoneSettings
import com.drbrosdev.extractor.ui.components.extractorsettings.ExtractorSettings
import com.drbrosdev.extractor.ui.components.extractorsettings.ExtractorSettingsState
import com.drbrosdev.extractor.ui.components.settingstabs.ExtractorSettingsTabContainer
import com.drbrosdev.extractor.ui.components.settingstabs.SettingsTabItem
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorHeader
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState
import com.drbrosdev.extractor.util.applicationIconResource


@Composable
fun ExtractorSettingsScreen(
    onBack: () -> Unit,
    onLicenseClick: (link: String) -> Unit,
    onPeriodicSyncClick: () -> Unit,
    onAboutLink: (AboutLink) -> Unit,
    onResetIndex: () -> Unit,
    onClearEventLogs: () -> Unit,
    modifier: Modifier = Modifier,
    settingsState: ExtractorSettingsState
) {
    val lazyListState = rememberLazyListState()
    val extractorTopBarState = remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex > 0) ExtractorTopBarState.ELEVATED
            else ExtractorTopBarState.NORMAL
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        constraintSet = settingsScreenConstraints()
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .layoutId(ViewIds.LAZY_CONTENT)
                .then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item(key = "app_info") { AppInfo() }

            item(key = "mid_spacer") { Spacer(modifier = Modifier.height(12.dp)) }

            item(key = "action_chips") {
                ExtractorActionChips(onActionClick = onAboutLink)
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item(key = "settings_tabs") {
                ExtractorSettingsTabContainer(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                ) {
                    when (it) {
                        SettingsTabItem.Settings -> {
                            Column {
                                ExtractorSettings(
                                    onPeriodicSyncClick = onPeriodicSyncClick,
                                    state = settingsState
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                ExtractorRedZoneSettings(
                                    onResetIndex = onResetIndex,
                                    onClearEventLogs = onClearEventLogs
                                )
                            }
                        }

                        SettingsTabItem.About -> ExtractorAbout()
                        SettingsTabItem.Licenses -> ExtractorLicenses(onClick = onLicenseClick)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(200.dp))
            }
        }

        ExtractorTopBar(
            modifier = Modifier.layoutId(ViewIds.TOP_BAR),
            state = extractorTopBarState.value,
            leadingSlot = {
                BackIconButton(onBack = onBack)
                ExtractorHeader(headerText = stringResource(id = R.string.settings))
            },
            trailingSlot = {
                Spacer(modifier = Modifier.width(12.dp))
            }
        )
    }
}

@Composable
private fun AppInfo(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(painter = applicationIconResource(), contentDescription = "Lupa Icon")
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color.Gray,
                fontWeight = FontWeight.Light
            )
        )
    }
}

private fun settingsScreenConstraints() = ConstraintSet {
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val content = createRefFor(ViewIds.LAZY_CONTENT)

    constrain(topBar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top)
        width = Dimension.fillToConstraints
    }

    constrain(content) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(topBar.bottom, margin = 4.dp)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val TOP_BAR = "topBar"
    const val LAZY_CONTENT = "lazyContent"
}