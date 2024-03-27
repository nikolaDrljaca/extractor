package com.drbrosdev.extractor.ui.components.actionchips

import com.drbrosdev.extractor.R

enum class AboutLink(
    val nameResource: Int,
    val iconResource: Int
) {
    WEBSITE(
        nameResource = R.string.action_website,
        iconResource = R.drawable.round_public_24
    ),
    POLICY(
        nameResource = R.string.action_policy,
        iconResource = R.drawable.round_public_24
    ),
    SHARE(
        nameResource = R.string.action_share,
        iconResource = R.drawable.round_share_24
    ),
    RATE(
        nameResource = R.string.action_rate,
        iconResource = R.drawable.round_star_24
    ),
    FEEDBACK(
        nameResource = R.string.action_feedback,
        iconResource = R.drawable.round_feedback_24
    ),
}
