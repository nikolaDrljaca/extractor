package com.drbrosdev.extractor.ui.permhandler

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.ExtractorButton
import com.drbrosdev.extractor.ui.components.shared.OutlinedExtractorActionButton
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview


@Composable
fun ExtractorPermissionRequestScreen(
    onOpenSettings: () -> Unit,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .systemBarsPadding()
            .then(modifier),
        constraintSet = permissionRequestScreenConstraintSet()
    ) {
        Column(
            modifier = Modifier.layoutId(ViewIds.WARNING),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier
                        .size(56.dp)
                        .padding(8.dp)
                )
            }
            Text(text = stringResource(R.string.required_permission), style = MaterialTheme.typography.headlineSmall)
            Text(text = stringResource(R.string.not_granted), style = MaterialTheme.typography.headlineLarge)
        }

        Column(
            modifier = Modifier.layoutId(ViewIds.RATIONALE),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.media_perm_rationale),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 14.sp
                )
            )

            Text(
                text = stringResource(R.string.media_perm_action),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 14.sp
                )
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.layoutId(ViewIds.ACTION_ROW)
        ) {
            ExtractorButton(onClick = onRequestPermission) {
                Text(text = stringResource(R.string.grant_permission))
            }

            OutlinedExtractorActionButton(onClick = onOpenSettings) {
                Text(text = stringResource(R.string.open_settings))
            }
        }
    }
}



private fun permissionRequestScreenConstraintSet() = ConstraintSet {
    val warning = createRefFor(ViewIds.WARNING)
    val rationale = createRefFor(ViewIds.RATIONALE)
    val actionRow = createRefFor(ViewIds.ACTION_ROW)

    val topGuideline = createGuidelineFromTop(0.20f)

    constrain(warning) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(topGuideline)
        width = Dimension.fillToConstraints
    }

    constrain(rationale) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(warning.bottom, margin = 12.dp)
        width = Dimension.fillToConstraints
    }

    constrain(actionRow) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(rationale.bottom, margin = 36.dp)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val WARNING = "warning"
    const val RATIONALE = "rationale"
    const val ACTION_ROW = "actionRow"
}
