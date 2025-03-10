package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun InfoIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    IconButton(
        modifier = Modifier.then(modifier),
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.Rounded.Info,
            contentDescription = "Info"
        )
    }
}
