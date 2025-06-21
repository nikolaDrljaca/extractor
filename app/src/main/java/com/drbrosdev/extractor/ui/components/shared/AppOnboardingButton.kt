package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppOnboardingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable (size: Dp) -> Unit
) {
    val size = ButtonDefaults.MediumContainerHeight
    Button(
        onClick = onClick,
        modifier = Modifier
            .heightIn(size)
            .fillMaxWidth()
            .then(modifier),
        contentPadding = ButtonDefaults.contentPaddingFor(size),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        content(size)
    }
}
