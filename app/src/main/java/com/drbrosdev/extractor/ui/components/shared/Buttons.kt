package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@Composable
fun ExtractorActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ExtractorButtonDefaults.paddingValues(),
    content: @Composable (RowScope.() -> Unit)
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .then(modifier),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = Color.LightGray,
            disabledContainerColor = Color.Gray
        ),
        enabled = enabled,
        contentPadding = contentPadding,
        shape = RoundedCornerShape(18.dp)
    ) {
        content()
    }
}

@Composable
fun ExtractorButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ExtractorButtonDefaults.paddingValues(),
    colors: ButtonColors = ExtractorButtonDefaults.colors(),
    content: @Composable (RowScope.() -> Unit)
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .then(modifier),
        colors = colors,
        enabled = enabled,
        contentPadding = contentPadding,
        shape = RoundedCornerShape(18.dp)
    ) {
        content()
    }
}

@Composable
fun OutlinedExtractorActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.primary
    },
    contentPadding: PaddingValues = ExtractorButtonDefaults.paddingValues(),
    content: @Composable (RowScope.() -> Unit)
) {

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .then(modifier),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor
        ),
        contentPadding = contentPadding,
        shape = RoundedCornerShape(18.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            brush = SolidColor(contentColor)
        )
    ) {
        content()
    }
}

@Composable
fun ExtractorTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    content: @Composable RowScope.() -> Unit
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = contentColor
        ),
        modifier = Modifier
            .then(modifier)
    ) {
        content()
    }
}

@Composable
fun BottomSheetButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (RowScope.() -> Unit)
) {
    OutlinedButton(
        modifier = Modifier.then(modifier),
        onClick = onClick,
        border = BorderStroke(
            width = 1.dp, color = Color.White
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Black,
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        content()
    }
}


object ExtractorButtonDefaults {
    private val leftContentPadding = ButtonDefaults.ContentPadding.calculateLeftPadding(
        LayoutDirection.Ltr
    )

    private val rightContentPadding = ButtonDefaults.ContentPadding.calculateRightPadding(
        LayoutDirection.Ltr
    )

    @Composable
    fun paddingValues(
        top: Dp = 16.dp,
        bottom: Dp = 16.dp
    ): PaddingValues = PaddingValues(
        start = leftContentPadding,
        end = rightContentPadding,
        top = top,
        bottom = bottom
    )

    @Composable
    fun paddingValues(vertical: Dp = 16.dp): PaddingValues = PaddingValues(
        start = leftContentPadding,
        end = rightContentPadding,
        top = vertical,
        bottom = vertical
    )

    @Composable
    fun colors() = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContentColor = Color.LightGray,
        disabledContainerColor = Color.Gray
    )
}

@CombinedPreview
@Composable
private fun ButtonsPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                OutlinedExtractorActionButton(onClick = { /*TODO*/ }) {
                    Text(text = "Action")
                }
                ExtractorActionButton(onClick = { /*TODO*/ }) {
                    Text(text = "Action")
                }
                ExtractorButton(onClick = { /*TODO*/ }) {
                    Text(text = "Action")
                }
                BottomSheetButton(onClick = { /*TODO*/ }) {
                    Text(text = "Action")
                }
                ExtractorTextButton(onClick = { /*TODO*/ }) {
                    Text(text = "Action")
                }
            }
        }
    }
}