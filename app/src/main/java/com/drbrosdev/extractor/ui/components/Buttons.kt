package com.drbrosdev.extractor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.ui.theme.md_theme_light_primary

@Composable
fun ExtractorActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (RowScope.() -> Unit)
) {
    val verticalContentPadding = 24.dp
    val leftContentPadding = ButtonDefaults.ContentPadding.calculateLeftPadding(
        LayoutDirection.Ltr
    )
    val rightContentPadding = ButtonDefaults.ContentPadding.calculateRightPadding(
        LayoutDirection.Ltr
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .then(modifier),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = md_theme_light_primary
        ),
        contentPadding = PaddingValues(
            top = verticalContentPadding,
            bottom = verticalContentPadding,
            start = leftContentPadding,
            end = rightContentPadding
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        content()
    }
}


@Composable
fun OutlinedExtractorActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (RowScope.() -> Unit)
) {
    val verticalContentPadding = 24.dp

    val leftContentPadding = ButtonDefaults.ContentPadding.calculateLeftPadding(
        LayoutDirection.Ltr
    )

    val rightContentPadding = ButtonDefaults.ContentPadding.calculateRightPadding(
        LayoutDirection.Ltr
    )

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .then(modifier),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(
            top = verticalContentPadding,
            bottom = verticalContentPadding,
            start = leftContentPadding,
            end = rightContentPadding
        ),
        shape = RoundedCornerShape(18.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            brush = Brush.linearGradient(
                listOf(Color.White, Color.White)
            )
        )
    ) {
        content()
    }
}

@Preview
@Composable
private fun ButtonsPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            OutlinedExtractorActionButton(onClick = { /*TODO*/ }) {
                Text(text = "Action")
            }
            ExtractorActionButton(onClick = { /*TODO*/ }) {
                Text(text = "Action")
            }
        }
    }
}