package com.drbrosdev.extractor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.ui.theme.md_theme_light_primary
import com.drbrosdev.extractor.util.applicationIconBitmap
import kotlinx.coroutines.delay

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

@Composable
fun ExtractorLeaderButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var shouldShowInfoText by rememberSaveable {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = Unit) {
        delay(3500L)
        shouldShowInfoText = false
    }

    Surface(
        modifier = Modifier
            .then(modifier),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(14.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .width(180.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.Start
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = applicationIconBitmap(),
                contentDescription = "App Icon",
                modifier = Modifier.size(48.dp)
            )

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )

                AnimatedVisibility(
                    visible = shouldShowInfoText,
                ) {
                    Text(
                        text = "Tap for more.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ButtonsPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
//            ExtractorLeaderButton(onClick = {})
            OutlinedExtractorActionButton(onClick = { /*TODO*/ }) {
                Text(text = "Action")
            }
            ExtractorActionButton(onClick = { /*TODO*/ }) {
                Text(text = "Action")
            }
        }
    }
}