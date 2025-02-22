package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun AttentionContainer(
    modifier: Modifier = Modifier,
    header: String,
    actionRow: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
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
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.width(8.dp))

            //header
            Text(
                text = header,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(vertical = 12.dp)
        ) {
            //content slot, column
            CompositionLocalProvider(
                LocalTextStyle provides contentTextStyle
            ) {
                content()
            }
        }

        // button row slot
        Row(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            actionRow?.invoke(this)
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AttentionContainer(header = "Header", modifier = Modifier.padding(4.dp)) {
                Text(
                    text = "Viewing ads will transfer some of your non-identifiable information to Google, the provider of the ad platform. No data will be transferred if you do not consent to the policy provided by Google Ads.",
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "You can find out more on our privacy policy page.",
                )
            }
        }
    }
}