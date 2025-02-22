package com.drbrosdev.extractor.ui.components.shared

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.ui.theme.md_theme_light_primary
import com.drbrosdev.extractor.util.thenIf

@Composable
fun OnboardingCard(
    modifier: Modifier = Modifier,
    painter: @Composable () -> Unit = {},
    topBar: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxSize()
            .then(modifier),
        constraintSet = onboardingCardConstraints()
    ) {
        Row(
            modifier = Modifier
                .layoutId(ViewIds.TOP_BAR)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            topBar?.invoke(this)
        }

        Box(
            modifier = Modifier.layoutId(ViewIds.PAINTER),
            contentAlignment = Alignment.Center
        ) {
            painter()
        }

        Column(
            modifier = Modifier
                .layoutId(ViewIds.CONTENT)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

private fun onboardingCardConstraints() = ConstraintSet {
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val painter = createRefFor(ViewIds.PAINTER)
    val content = createRefFor(ViewIds.CONTENT)

    val topGuideline = createGuidelineFromTop(0.1f)
    val contentGuideline = createGuidelineFromTop(0.5f)

    constrain(topBar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
        bottom.linkTo(topGuideline)
    }

    constrain(painter) {
        top.linkTo(topBar.bottom)
        bottom.linkTo(contentGuideline)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
        height = Dimension.percent(0.35f)
    }

    constrain(content) {
        top.linkTo(painter.bottom)
        bottom.linkTo(parent.bottom)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val TOP_BAR = "topBarBar"
    const val PAINTER = "painter"
    const val CONTENT = "content"
}

@Composable
fun OnboardingCard(
    modifier: Modifier = Modifier,
    body: String,
    headline: (@Composable () -> Unit)? = null,
    actionButton: (@Composable () -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.elevatedCardColors(
            contentColor = Color.White,
            containerColor = md_theme_light_primary
        )
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            headline?.invoke()
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = body,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            )

            actionButton?.let {
                Spacer(modifier = Modifier.height(12.dp))
                it.invoke()
                Spacer(modifier = Modifier.height(8.dp))
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
            .thenIf(onBack == null) {
                padding(top = 12.dp)
            }
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        onBack?.let {
            IconButton(onClick = it) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Go back"
                )
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
        Column {
            OnboardingCard(
                body = stringResource(id = R.string.lorem),
                headline = { OnboardingCardHeadline(headline = "A note ", onBack = {}) },
                modifier = Modifier
                    .height(444.dp)
                    .width(344.dp)
            ) {
                OutlinedExtractorActionButton(onClick = { /*TODO*/ }) {
                    Text(
                        text = "Sample Action",
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            OnboardingCard(
                body = stringResource(id = R.string.lorem),
                headline = { OnboardingCardHeadline(headline = "A note ") },
                modifier = Modifier
                    .height(444.dp)
                    .width(344.dp)
            ) {
                OutlinedExtractorActionButton(onClick = { /*TODO*/ }) {
                    Text(
                        text = "Sample Action",
                    )
                }
            }
        }
    }
}