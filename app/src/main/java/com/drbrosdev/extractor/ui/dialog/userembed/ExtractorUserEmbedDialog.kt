package com.drbrosdev.extractor.ui.dialog.userembed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.ExtractorButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorButtonDefaults
import com.drbrosdev.extractor.ui.components.shared.ExtractorSuggestedEmbeddingChips
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextFieldState

@Composable
fun ExtractorUserEmbedDialog(
    modifier: Modifier = Modifier,
    onCreateNew: () -> Unit,
    onCheck: (String) -> Unit,
    onSave: () -> Unit,
    suggestedEmbedsState: ExtractorSuggestedEmbedsUiState,
    textFieldState: ExtractorTextFieldState,
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
                    text = stringResource(R.string.custom_keywords),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall
                )

                ExtractorButton(
                    onClick = onSave,
                    modifier = Modifier,
                    contentPadding = ExtractorButtonDefaults.paddingValues(vertical = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_save_24),
                        contentDescription = "null"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.info_screen_save))
                }
            }

            // suggestion chips
            Column {
                Text(
                    text = stringResource(R.string.existing_keywords),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.click_one_to_add_to_this_image),
                    style = smallLabel
                )
            }

            when (suggestedEmbedsState) {
                ExtractorSuggestedEmbedsUiState.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            strokeCap = StrokeCap.Round,
                            color = MaterialTheme.colorScheme.onSurface,
                            trackColor = Color.Transparent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                ExtractorSuggestedEmbedsUiState.Empty -> {
                    Spacer(modifier = Modifier.height(18.dp))
                }

                is ExtractorSuggestedEmbedsUiState.Content -> {
                    ExtractorSuggestedEmbeddingChips(
                        onClick = onCheck,
                        embeddings = suggestedEmbedsState.suggestions
                    )
                }
            }

            // Text Field
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.enter_new),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.add_multiple_user),
                    style = smallLabel
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = textFieldState.textValue,
                    onValueChange = textFieldState::updateTextValue,
                    label = { Text(text = stringResource(R.string.custom_keyword)) },
                    keyboardActions = KeyboardActions(
                        onDone = { onCreateNew() }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = true,
                    maxLines = 1,
                    minLines = 1
                )
            }
        }
    }
}
