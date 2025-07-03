package com.drbrosdev.extractor.ui.components.extractorabout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@Composable
fun ExtractorAbout(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.tab_about_us),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 14.sp
            )
        )
        Text(
            text = stringResource(id = R.string.tab_about_content),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 14.sp,
            )
        )
        Text(text = stringResource(R.string.made_in_wien))
    }
}


@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface {
            ExtractorAbout()
        }
    }
}