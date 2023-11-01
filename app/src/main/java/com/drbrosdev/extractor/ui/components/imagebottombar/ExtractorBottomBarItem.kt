package com.drbrosdev.extractor.ui.components.imagebottombar

import com.drbrosdev.extractor.R

enum class ExtractorBottomBarItem(
    val text: String,
    val iconRes: Int
) {
    SHARE(text = "Share", iconRes = R.drawable.round_share_24),
    EDIT(text = "Edit", iconRes = R.drawable.round_edit_24),
    USE_AS(text = "Use as", iconRes = R.drawable.round_launch_24),
    EX_INFO(text = "EX Info", iconRes = R.drawable.baseline_android_24)
}