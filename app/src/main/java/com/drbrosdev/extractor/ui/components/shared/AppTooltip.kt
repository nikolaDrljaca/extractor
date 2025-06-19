package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.theme.inverseOnSurfaceLightMediumContrast
import com.drbrosdev.extractor.ui.theme.inverseSurfaceLightMediumContrast


@Composable
fun AppTooltip(
    modifier: Modifier = Modifier,
    text: String
) {
    AppTooltip(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Justify,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

@Composable
fun AppTooltip(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
//        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
        contentColor = inverseOnSurfaceLightMediumContrast,
//        color = MaterialTheme.colorScheme.inverseSurface,
        color = inverseSurfaceLightMediumContrast,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
