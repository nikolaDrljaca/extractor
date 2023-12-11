package com.drbrosdev.extractor.ui.components.imagebottombar

import com.drbrosdev.extractor.R

enum class ExtractorBottomBarItem(
    val stringRes: Int,
    val iconRes: Int
) {
    SHARE(stringRes = R.string.bottom_bar_share, iconRes = R.drawable.round_share_24),
    EDIT(stringRes = R.string.bottom_bar_edit, iconRes = R.drawable.round_edit_24),
    USE_AS(stringRes = R.string.bottom_bar_use_as, iconRes = R.drawable.round_launch_24),
    EX_INFO(stringRes = R.string.bottom_bar_ex_info, iconRes = R.drawable.baseline_android_24)
}