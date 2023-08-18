package com.drbrosdev.extractor.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

@Composable
fun PreviousSearchItem(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(8.dp))
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )

            IconButton(onClick = { onDelete() }) {
                Icon(imageVector = Icons.Rounded.Clear, contentDescription = null)
            }
        }
    }
}

@Composable
fun SearchTopBar(
    modifier: Modifier = Modifier,
    localCount: Int = 30,
    deviceCount: Int = 40,
    onClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .clip(RoundedCornerShape(8.dp))
                .padding(4.dp)
                .weight(1f)
                .then(modifier),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Extraction status: $localCount/$deviceCount",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tap here to run extraction.",
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        IconButton(
            onClick = onAboutClick
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "About App",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    initialQuery: String = "",
    onDone: (String) -> Unit
) {
    val (text, setText) = rememberSaveable {
        mutableStateOf(initialQuery)
    }

    Row(
        modifier = Modifier
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExtractorTextField(
            modifier = Modifier.weight(1f),
            text = text,
            onChange = { setText(it) },
            onDoneSubmit = { onDone(text) }
        )
        Button(
            onClick = { onDone(text) },
            shape = RoundedCornerShape(8.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search icon",
            )
        }
    }
}


