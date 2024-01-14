package com.drbrosdev.extractor.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.BuildConfig
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorActionChip
import com.drbrosdev.extractor.ui.components.shared.ExtractorHeader
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState


@Composable
fun ExtractorSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
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
                ActionChips(onActionClick = {})
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item(key = "settings_tabs") {
                SettingsTabs()
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

sealed class SettingsTabItem {
    data object Settings : SettingsTabItem()

    data object Licenses : SettingsTabItem()

    data object About : SettingsTabItem()
}

val tabItems = listOf(
    SettingsTabItem.Settings,
    SettingsTabItem.Licenses,
    SettingsTabItem.About,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTabs(
    modifier: Modifier = Modifier,
) {
    var selectedIndex by remember {
        mutableStateOf(0)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        PrimaryTabRow(
            selectedTabIndex = selectedIndex,
            indicator = {
                ExtractorTabIndicator(
                    Modifier.tabIndicatorOffset(it[selectedIndex]),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            },
            divider = {},
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .zIndex(1f),
                    selected = index == selectedIndex,
                    onClick = { selectedIndex = index },
                    selectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    Text(
                        text = "Tab $index",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        //switch content based on index
        Box(
            modifier = Modifier
                .size(250.dp)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Tab $selectedIndex selected")
        }
    }
}

@Composable
private fun ExtractorTabIndicator(
    modifier: Modifier = Modifier,
    color: Color
) {
    Box(
        modifier
            .fillMaxSize()
            .padding(4.dp)
            .clip(CircleShape)
            .zIndex(0f)
            .background(color = color)
    )
}


enum class Action(
    val nameResource: Int,
    val iconResource: Int
) {
    WEBSITE(
        nameResource = R.string.action_website,
        iconResource = R.drawable.round_public_24
    ),
    POLICY(
        nameResource = R.string.action_policy,
        iconResource = R.drawable.round_public_24
    ),
    SHARE(
        nameResource = R.string.action_share,
        iconResource = R.drawable.round_share_24
    ),
    RATE(
        nameResource = R.string.action_rate,
        iconResource = R.drawable.round_star_24
    ),
    BUG_REPORT(
        nameResource = R.string.action_bug,
        iconResource = R.drawable.round_bug_report_24
    ),
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActionChips(
    onActionClick: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.spacedBy(
            space = 2.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        Action.entries.forEach {
            ExtractorActionChip(
                onClick = { onActionClick(it) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = it.iconResource),
                        contentDescription = null
                    )
                }
            ) {
                Text(text = stringResource(id = it.nameResource))
            }
        }
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
//                Image(bitmap = applicationIconBitmap(), contentDescription = "")
        Image(
            painter = painterResource(id = R.drawable.baseline_android_24),
            contentDescription = null,
            modifier = Modifier.size(56.dp)
        )
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Version ${BuildConfig.VERSION_NAME}",
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