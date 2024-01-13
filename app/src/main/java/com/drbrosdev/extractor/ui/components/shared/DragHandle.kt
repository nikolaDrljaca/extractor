package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun DragHandle(
    modifier: Modifier = Modifier,
    height: Dp = DragHandleDefaults.height,
    width: Dp = DragHandleDefaults.width,
    shape: Shape = DragHandleDefaults.shape,
    color: Color = DragHandleDefaults.color
) {
    Surface(
        modifier = modifier
            .padding(vertical = 11.dp),
        color = color.copy(alpha = 0.5f),
        shape = shape
    ) {
        Box(
            Modifier
                .size(
                    width = width,
                    height = height
                )
        )
    }
}

object DragHandleDefaults {
    val height = 4.0.dp
    val width = 32.dp
    val shape = RoundedCornerShape(28.dp)
    val color = Color.White
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        DragHandle()
    }
}