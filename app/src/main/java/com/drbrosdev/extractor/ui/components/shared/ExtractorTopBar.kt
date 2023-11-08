package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun ExtractorTopBar(
    modifier: Modifier = Modifier,
    leadingSlot: @Composable RowScope.() -> Unit,
    centerSlot: @Composable RowScope.() -> Unit,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        leadingSlot()
        centerSlot()
        trailingSlot?.invoke(this)
    }
}
