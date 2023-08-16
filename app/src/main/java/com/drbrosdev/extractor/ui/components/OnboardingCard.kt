package com.drbrosdev.extractor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@Composable
fun OnboardingCard(
    modifier: Modifier = Modifier,
    headline: String,
    body: String,
    actionButton: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                12.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = headline, style = MaterialTheme.typography.headlineSmall)
            Text(text = body)

            actionButton?.let {
                it.invoke()
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview
@Composable
private fun OnboardingCardPreview() {
    ExtractorTheme {
        OnboardingCard(headline = "Headline", body = stringResource(id = R.string.lorem)) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Sample Action")
            }
        }
    }
}