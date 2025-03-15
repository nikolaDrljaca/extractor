package com.drbrosdev.extractor.ui.components.extractorsettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview


@Composable
fun ExtractorSettings(
    modifier: Modifier = Modifier,
    onPeriodicSyncClick: () -> Unit,
    state: ExtractorSettingsState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.then(modifier)
    ) {
        ExtractorSettingsItem(
            itemPosition = ExtractorSettingsItemPosition.FIRST,
            trailingSlot = {
                Switch(
                    checked = state.isDynamicColorEnabled,
                    onCheckedChange = { state.onDynamicColorChanged(it) }
                )
            }
        ) {
            Text(text = stringResource(R.string.use_material_you_theme))
        }

        ExtractorSettingsItem(
            itemPosition = ExtractorSettingsItemPosition.LAST,
            onClick = onPeriodicSyncClick,
            trailingSlot = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.periodic_sync),
                    modifier = Modifier.minimumInteractiveComponentSize()
                )
            }
        ) {
            Text(text = stringResource(R.string.periodic_sync))
        }
    }
}

enum class ExtractorSettingsItemPosition {
    FIRST,
    NORMAL,
    LAST
}

@Composable
fun ExtractorSettingsItem(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    itemPosition: ExtractorSettingsItemPosition = ExtractorSettingsItemPosition.NORMAL,
    onClick: () -> Unit,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    slot: @Composable RowScope.() -> Unit
) {
    val shape = when (itemPosition) {
        ExtractorSettingsItemPosition.FIRST -> RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomEnd = 2.dp,
            bottomStart = 2.dp
        )

        ExtractorSettingsItemPosition.NORMAL -> RoundedCornerShape(2.dp)
        ExtractorSettingsItemPosition.LAST -> RoundedCornerShape(
            topStart = 2.dp,
            topEnd = 2.dp,
            bottomEnd = 12.dp,
            bottomStart = 12.dp
        )
    }

    Surface(
        onClick = onClick,
        color = color,
        shape = shape,
        modifier = Modifier
            .then(modifier),
        contentColor = contentColor,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .defaultMinSize(minHeight = 56.dp)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            slot()
            trailingSlot?.invoke(this)
        }
    }
}

@Composable
fun ExtractorSettingsItem(
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    itemPosition: ExtractorSettingsItemPosition = ExtractorSettingsItemPosition.NORMAL,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    slot: @Composable RowScope.() -> Unit
) {
    val shape = when (itemPosition) {
        ExtractorSettingsItemPosition.FIRST -> RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomEnd = 2.dp,
            bottomStart = 2.dp
        )

        ExtractorSettingsItemPosition.NORMAL -> RoundedCornerShape(2.dp)
        ExtractorSettingsItemPosition.LAST -> RoundedCornerShape(
            topStart = 2.dp,
            topEnd = 2.dp,
            bottomEnd = 12.dp,
            bottomStart = 12.dp
        )
    }

    Surface(
        color = color,
        shape = shape,
        modifier = Modifier
            .then(modifier),
        contentColor = contentColor
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .defaultMinSize(minHeight = 56.dp)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            slot()
            trailingSlot?.invoke(this)
        }
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorSettings(
            state = ExtractorSettingsState(
                isDynamicColorEnabled = false,
                onDynamicColorChanged = {}
            ),
            onPeriodicSyncClick = {}
        )
    }
}
