package com.drbrosdev.extractor.ui.dialog.userembed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.framework.navigation.DialogNavTarget
import com.drbrosdev.extractor.ui.components.shared.ExtractorButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorEmbeddingChips
import com.drbrosdev.extractor.ui.imageinfo.UserEmbedUiModel
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorUserEmbedDialogNavTarget(
    private val mediaImageId: Long
) : DialogNavTarget {

    @Composable
    override fun Content() {
        val viewModel: ExtractorUserEmbedViewModel = koinViewModel {
            parametersOf(mediaImageId)
        }

        ExtractorUserEmbedDialog()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExtractorUserEmbedDialog(
    modifier: Modifier = Modifier
) {
    val smallLabel = MaterialTheme.typography.labelSmall.copy(
        color = Color.Gray
    )

    Surface(
        modifier = Modifier
            .then(modifier),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 12.dp)
        ) {
            // Title and Save button
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Custom Keywords",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall
                )

                ExtractorButton(
                    onClick = {},
                    modifier = Modifier,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_save_24),
                        contentDescription = "null"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.info_screen_save))
                }
            }

            Column {
                Text(
                    text = "Existing Keywords",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Click one to add to this image.",
                    style = smallLabel
                )
            }
            //TODO these need to be outlined and with no icons
            // material.SuggestionChip
            ExtractorEmbeddingChips(
                onClear = {},
                embeddings = listOf(
                    UserEmbedUiModel("first", false),
                    UserEmbedUiModel("second", false),
                    UserEmbedUiModel("third", false),
                    UserEmbedUiModel("third", false),
                    UserEmbedUiModel("third", false),
                    UserEmbedUiModel("third", false),
                )
            )

            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Enter New",
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = "",
                    onValueChange = {},
                    label = { Text(text = "Custom Keyword") },
                )
            }
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        ExtractorUserEmbedDialog()
    }
}