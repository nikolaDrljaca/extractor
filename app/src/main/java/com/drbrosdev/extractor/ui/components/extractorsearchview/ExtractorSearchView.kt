package com.drbrosdev.extractor.ui.components.extractorsearchview

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.usecase.LabelType
import com.drbrosdev.extractor.ui.components.datafilterchip.ImageLabelFilterChips
import com.drbrosdev.extractor.ui.components.datafilterchip.toLabelType
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextField
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@Composable
fun ExtractorSearchView(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    state: ExtractorSearchViewState,
) {
    Surface(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.primary,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ExtractorTextField(
                text = state.query,
                onChange = { state.query = it },
                onDoneSubmit = onDone,
                textColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            ImageLabelFilterChips(
                onFilterChanged = {
                    state.labelType = it.toLabelType()
                },
                contentColor = Color.White
            )
        }
    }
}



@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        ExtractorSearchView(
            onDone = {},
            state = object : ExtractorSearchViewState {
                override var query: String
                    get() = "some query"
                    set(value) {}
                override var labelType: LabelType
                    get() = LabelType.ALL
                    set(value) {}
            }
        )
    }
}
