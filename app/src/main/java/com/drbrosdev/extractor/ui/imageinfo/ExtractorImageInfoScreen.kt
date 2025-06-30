package com.drbrosdev.extractor.ui.imageinfo

import android.content.ClipData
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.framework.requiresApi
import com.drbrosdev.extractor.ui.components.shared.AppImageInfoHeader
import com.drbrosdev.extractor.util.launchShareIntent
import com.drbrosdev.extractor.util.launchTranslateIntent
import kotlinx.coroutines.launch

@Composable
fun LupaImageInfoScreen(
    modifier: Modifier = Modifier,
    model: LupaImageInfoState
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
            .systemBarsPadding()
    ) {
        AppImageInfoHeader(
            modifier = Modifier,
            model = model.heading
        )

        Box(Modifier) {
            when {
                model.hasDescription -> AppImageDetailDescription(
                    text = model.description!!,
                    modifier = Modifier.fillMaxWidth()
                )

                else -> Spacer(Modifier.height(16.dp))
            }
        }

        AppImageDetailEditable(
            modifier = Modifier
                .padding(top = 8.dp),
            model = model.editables
        )
    }
}

// ======

@Composable
private fun AppImageDetailDescription(
    modifier: Modifier = Modifier,
    text: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text(
                text = "Gemini says",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = text,
            )
        }
    }
}

@Composable
private fun ImageAnnotationsFlowRow(
    modifier: Modifier = Modifier,
    lupaImageAnnotationsState: LupaImageAnnotationsState
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(top = 4.dp)
    ) {
        lupaImageAnnotationsState.embeds.forEach { embed ->
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Text(
                    text = embed,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// share copy translate
private enum class SupportBarAction(
    val label: Int,
    val drawable: Int
) {
    SHARE(R.string.action_share, R.drawable.round_share_24),
    COPY(R.string.action_copy, R.drawable.rounded_content_copy_24),
    TRANSLATE(R.string.action_translate, R.drawable.outline_g_translate_24)
}

private typealias SupportBarHandler = (action: SupportBarAction) -> Unit

@Composable
private fun rememberSupportActionBarHandler(
    value: String
): SupportBarHandler {
    val context = LocalContext.current
    val clipboardManager = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    val handler: SupportBarHandler = { action ->
        when (action) {
            SupportBarAction.SHARE -> context.launchShareIntent(value)
            SupportBarAction.TRANSLATE -> context.launchTranslateIntent(value)
            SupportBarAction.COPY -> coroutineScope.launch {
                val entry = ClipData.newPlainText("plain text", value)
                    .toClipEntry()
                clipboardManager.setClipEntry(entry)
                requiresApi(Build.VERSION_CODES.S_V2) {
                    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    return remember { handler }
}

@Composable
private fun TextAnnotationSupportBar(
    modifier: Modifier = Modifier,
    handler: SupportBarHandler
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        SupportBarAction.entries.forEach {
            AssistChip(
                onClick = { handler(it) },
                label = { Text(stringResource(it.label)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(it.drawable),
                        contentDescription = stringResource(it.label)
                    )
                }
            )
        }
    }
}

@Composable
private fun AppImageDetailEditable(
    modifier: Modifier = Modifier,
    model: LupaImageEditablesState
) {
    val islandSpacer = 16.dp
    Column(
        modifier = Modifier
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(islandSpacer)
    ) {
        EditableContentIsland(
            modifier = Modifier,
            onClick = { model.eventSink(LupaImageEditablesEvents.OnVisualEdit) },
            title = { Text(stringResource(R.string.visual_embeddings)) }
        ) {
            ImageAnnotationsFlowRow(lupaImageAnnotationsState = model.visualEmbeds)
        }

        EditableContentIsland(
            modifier = Modifier,
            onClick = { model.eventSink(LupaImageEditablesEvents.OnUserEdit) },
            title = { Text(stringResource(R.string.user_embeddings)) }
        ) {
            ImageAnnotationsFlowRow(lupaImageAnnotationsState = model.userEmbeds)
        }

        EditableContentIsland(
            onClick = { model.eventSink(LupaImageEditablesEvents.OnTextEdit) },
            title = { Text(stringResource(R.string.text_embeddings)) }
        ) {
            if (model.isTextBlank.not()) {
                TextAnnotationSupportBar(
                    modifier = Modifier.fillMaxWidth(),
                    handler = rememberSupportActionBarHandler(model.textEmbed)
                )
            }
            Text(
                text = model.textEmbed,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun EditableContentIsland(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    title: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.titleLarge
                ) {
                    title()
                }
                IconButton(
                    onClick = onClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "edit content",
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }
            content()
        }
    }
}

