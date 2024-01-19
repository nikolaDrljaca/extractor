package com.drbrosdev.extractor.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview


@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_7
)
@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_7
)
annotation class ScreenPreview


@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class CombinedPreview

