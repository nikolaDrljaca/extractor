package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BackIconButton(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    enabled: Boolean = true
) {

    IconButton(
        modifier = Modifier.then(modifier),
        onClick = onBack,
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = "Go Back"
        )
    }
}