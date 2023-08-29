package com.drbrosdev.extractor.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.ui.theme.md_theme_light_primary
import com.drbrosdev.extractor.ui.theme.md_theme_light_secondary


@Composable
fun OnboardingCard(
    modifier: Modifier = Modifier,
    body: String,
    headline: (@Composable () -> Unit)? = null,
    actionButton: (@Composable () -> Unit)? = null,
) {
    val brush = Brush.linearGradient(
        listOf(
            md_theme_light_primary,
            md_theme_light_secondary
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.elevatedCardColors(
            contentColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .background(brush)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                headline?.invoke()
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = body, modifier = Modifier.weight(1f))

                actionButton?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    it.invoke()
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun OnboardingCardHeadline(
    modifier: Modifier = Modifier,
    headline: String,
    onBack: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        onBack?.let {
            IconButton(onClick = it) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        Text(text = headline, style = MaterialTheme.typography.headlineSmall)
    }
}


@Preview(uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun OnboardingCardPreview() {
    ExtractorTheme(dynamicColor = false) {
        OnboardingCard(
            body = stringResource(id = R.string.lorem),
            headline = { OnboardingCardHeadline(headline = "A note ", onBack = {}) }
        ) {
            OutlinedExtractorActionButton(onClick = { /*TODO*/ }) {
                Text(
                    text = "Sample Action",
                )
            }
        }
    }
}