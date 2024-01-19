package com.drbrosdev.extractor.ui.components.extractorabout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun ExtractorAbout(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lorem),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 14.sp
            )
        )
        Text(text = "Made with \u2764 in Vienna")
    }
}


@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        ExtractorAbout()
    }
}