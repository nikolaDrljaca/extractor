package com.drbrosdev.extractor.ui.settings.periodic

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar


@Composable
fun ExtractorPeriodicWorkScreen(
    onBack: () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textStyle = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Normal
    )
    val transition = updateTransition(targetState = checked, label = "")

    val surfaceColor by transition.animateColor(label = "") {
        if (it) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.inverseSurface
    }

    val surfaceContentColor by transition.animateColor(label = "") {
        if (it) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.inverseOnSurface
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            Text(text = stringResource(R.string.periodic_sync), style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.periodic_first_explainer),
                style = textStyle
            )

            Surface(
                modifier = Modifier.padding(vertical = 16.dp),
                color = surfaceColor,
                contentColor = surfaceContentColor,
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(R.string.use_periodic_sync), style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp))

                    Switch(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.onPrimary,
                            uncheckedTrackColor = Color.Gray,
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = Color.LightGray
                        )
                    )
                }
            }

            Text(
                text = stringResource(R.string.periodic_second_explainer),
                style = textStyle
            )
        }

        ExtractorTopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            leadingSlot = {
                BackIconButton(onBack = onBack)
            },
            trailingSlot = {
                Spacer(modifier = Modifier.width(12.dp))
            }
        )
    }

}