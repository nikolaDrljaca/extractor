package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.util.shimmer

@Composable
fun ContentPlaceholder(
    modifier: Modifier = Modifier,
    height: Int = 56
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .height(height.dp)
            .shimmer()
            .then(modifier)
    )
}

@Preview
@Composable
private fun CurrentPreview2() {
    ContentPlaceholder(
        modifier = Modifier.fillMaxWidth()
    )
}
