package com.drbrosdev.extractor.ui.components.extractorstatusbutton

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import androidx.work.await
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.worker.WorkNames
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.createExtractorBrush
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


@Composable
fun ExtractorStatusButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: ExtractorStatusButtonState,
) {
    val brushModifier = when (state) {
        is ExtractorStatusButtonState.Idle -> Modifier
        is ExtractorStatusButtonState.Working -> Modifier.background(createExtractorBrush())
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .then(brushModifier)
            .size(40.dp)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is ExtractorStatusButtonState.Idle ->
                Icon(
                    painter = painterResource(id = R.drawable.round_short_text_24),
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )

            is ExtractorStatusButtonState.Working ->
                Box(
                    modifier = Modifier.padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${state.donePercentage}%",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Column {
            ExtractorStatusButton(
                onClick = { /*TODO*/ },
                state = ExtractorStatusButtonState.Idle,
            )

            ExtractorStatusButton(
                onClick = { /*TODO*/ },
                state = ExtractorStatusButtonState.Working(34),
            )
        }
    }
}