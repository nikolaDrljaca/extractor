package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


private enum class SelectedView {
    CONSENT,
    BUTTON
}

@Composable
fun ExtractorViewAdContainer(
    modifier: Modifier = Modifier,
) {
    var selectedView by remember {
        mutableStateOf(SelectedView.CONSENT)
    }

    AdContainerLayout(
        modifier = modifier
    ) {
//        AdViewLoading(
//            modifier = Modifier.fillMaxWidth()
//        )

        AnimatedContent(
            targetState = selectedView,
            label = ""
        ) {
            when (it) {
                SelectedView.BUTTON -> Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedExtractorActionButton(
                        onClick = { selectedView = SelectedView.CONSENT },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.rounded_rewarded_ads_24),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "View Ad")
                    }

                    ExtractorTextButton(
                        onClick = { },
                        contentColor = MaterialTheme.colorScheme.tertiary
                    ) {
                        Text(text = "Review Policy")
                    }
                }

                SelectedView.CONSENT -> AdViewConsentBanner(
                    onViewFormClick = { selectedView = SelectedView.BUTTON },
                    onViewPolicyClick = {}
                )
            }
        }
    }
}

@Composable
private fun AdContainerLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val textStyle = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Normal
    )

    Column(
        modifier = Modifier
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.watch_an_ad),
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = stringResource(R.string.ad_support),
            style = textStyle
        )

        Spacer(modifier = Modifier.height(6.dp))

        content()
    }
}

@Composable
fun AdViewConsentBanner(
    modifier: Modifier = Modifier,
    onViewFormClick: () -> Unit,
    onViewPolicyClick: () -> Unit
) {
    val contentPadding = PaddingValues(start = 16.dp, end = 16.dp)

    val contentTextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Normal
    )

    OutlinedCard(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(18.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.rounded_release_alert_24),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Before you continue",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = "Viewing ads will transfer some of your non-identifiable information to Google, the provider of the ad platform. No data will be transferred if you do not consent to the policy provided by Google Ads.",
                style = contentTextStyle
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "You can find out more on our privacy policy page.",
                style = contentTextStyle
            )
        }

        // Button row
        Row(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            ExtractorTextButton(
                onClick = onViewPolicyClick,
                contentColor = MaterialTheme.colorScheme.tertiary
            ) {
                Text(text = "Privacy Policy")
            }
            ExtractorTextButton(
                onClick = onViewFormClick,
                contentColor = MaterialTheme.colorScheme.tertiary
            ) {
                Text(text = "View Form")
            }
        }
    }
}

@Composable
private fun AdViewLoading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.then(modifier),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            trackColor = Color.Transparent,
            color = MaterialTheme.colorScheme.tertiary,
            strokeCap = StrokeCap.Round
        )
    }
}


@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 30.dp)
        ) {
            AdViewConsentBanner(
                onViewFormClick = {},
                onViewPolicyClick = {}
            )
        }
    }
}