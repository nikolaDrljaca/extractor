package com.drbrosdev.extractor.ui.components.extractorsearchview

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextField
import com.drbrosdev.extractor.ui.components.datafilterchip.ImageLabelFilterChipData
import com.drbrosdev.extractor.ui.components.datafilterchip.ImageLabelFilterChips
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun ExtractorSearchView(
    onDone: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onFilterChanged: (ImageLabelFilterChipData) -> Unit,
    modifier: Modifier = Modifier,
    initialQuery: String = "",
) {
    val (text, setText) = rememberSaveable {
        mutableStateOf(initialQuery)
    }

    val elevation = if (isSystemInDarkTheme()) {
        0.dp
    } else {
        4.dp
    }

    Surface(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.primary,
        shadowElevation = elevation
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ExtractorTextField(
                text = text,
                onChange = {
                    setText(it)
                    onQueryChanged(it)
                },
                onDoneSubmit = onDone,
                textColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            ImageLabelFilterChips(
                onFilterChanged = onFilterChanged,
                contentColor = Color.White
            )
        }
    }
}


@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        ExtractorSearchView(onDone = {}, onFilterChanged = {}, onQueryChanged = {})
    }
}
