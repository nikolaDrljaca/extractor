package com.drbrosdev.extractor.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview


@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class ScreenPreview


@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class CombinedPreview

