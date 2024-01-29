package com.drbrosdev.extractor.ui.components.actionchips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableChipColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.components.shared.ExtractorChip
import com.drbrosdev.extractor.ui.components.shared.ExtractorChipDefaults
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExtractorActionChips(
    onActionClick: (AboutLink) -> Unit,
    modifier: Modifier = Modifier,
    colors: SelectableChipColors = ExtractorChipDefaults.surfaceChipColors()
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
        AboutLink.entries.forEach {
            ExtractorChip(
                onClick = { onActionClick(it) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = it.iconResource),
                        contentDescription = null
                    )
                },
                colors = colors,
                text = stringResource(id = it.nameResource)
            )
        }
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorActionChips(onActionClick = {})
    }
}
