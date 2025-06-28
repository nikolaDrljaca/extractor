package com.drbrosdev.extractor.ui.imageinfo.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.saveable
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.LupaImage
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

interface UserAnnotationsEditorComponent {
    val textState: TextFieldState
    val suggestions: StateFlow<List<String>>
    val annotations: StateFlow<List<String>>

    fun onDelete(value: String)
    fun onAddNew(value: String)
}

val previewUserEditor = object : UserAnnotationsEditorComponent {
    override val textState: TextFieldState
        get() = TextFieldState("")
    override val suggestions: StateFlow<List<String>>
        get() = MutableStateFlow(emptyList())
    override val annotations: StateFlow<List<String>>
        get() = MutableStateFlow(emptyList())

    override fun onDelete(value: String) = Unit

    override fun onAddNew(value: String) = Unit
}

class UserAnnotationsEditorComponentImpl(
    scope: CoroutineScope,
    stateHandle: SavedStateHandle,
    annotationsFlow: () -> Flow<LupaImage?>,
    private val getSuggestions: suspend () -> List<String>,
    private val createNewKeyword: (value: String) -> Unit,
    private val deleteKeyword: (value: String) -> Unit
) : UserAnnotationsEditorComponent {
    override val textState = stateHandle.saveable(
        key = "user_keyword_text",
        saver = TextFieldState.Saver,
        init = { TextFieldState(initialText = "") }
    )

    override val suggestions = flow { emit(getSuggestions()) }
        .stateIn(
            scope,
            WhileUiSubscribed,
            emptyList()
        )

    override val annotations = annotationsFlow()
        .map { it?.annotations?.userEmbeds ?: emptyList() }
        .stateIn(
            scope,
            WhileUiSubscribed,
            emptyList()
        )

    override fun onDelete(value: String) {
        deleteKeyword(value)
    }

    override fun onAddNew(value: String) {
        // check if already existing
        if (annotations.value.contains(value)) {
            return
        }
        createNewKeyword(value)
        // clear
        textState.clearText()
    }
}

@Composable
fun UserAnnotationsEditor(
    modifier: Modifier = Modifier,
    component: UserAnnotationsEditorComponent
) {
    val suggestions by component.suggestions.collectAsStateWithLifecycle()
    val annotations by component.annotations.collectAsStateWithLifecycle()
    val textState = component.textState

    val noUserKeywords by remember(annotations) {
        derivedStateOf {
            annotations.isEmpty()
        }
    }
    val isSuggested by remember(suggestions) {
        derivedStateOf { suggestions.isNotEmpty() }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.user_embeddings),
            style = MaterialTheme.typography.headlineSmall
        )
        // text field - for user
        OutlinedTextField(
            state = textState,
            onKeyboardAction = { component.onAddNew(textState.text.toString()) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            lineLimits = TextFieldLineLimits.SingleLine,
            placeholder = { Text(text = stringResource(R.string.custom_keyword)) },
            supportingText = {
                if (noUserKeywords) {
                    Text(text = "Add your own keywords!")
                }
            }
        )

        // keyword display with delete
        if (noUserKeywords.not()) {
            KeywordFlowRow(
                onClick = { component.onDelete(it) },
                values = annotations,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // suggested keywords - for user
        if (isSuggested) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.existing_keywords),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(R.string.click_one_to_add_to_this_image),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray
                    )
                )
                Spacer(Modifier.height(12.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestions.forEach {
                        SuggestionChip(
                            onClick = { component.onAddNew(it) },
                            label = { Text(it) },
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}
