package com.drbrosdev.extractor.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
    count: Int,
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
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(
                space = 2.dp, alignment = Alignment.CenterVertically
            )
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Result hits: $count",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontStyle = FontStyle.Italic
                )
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Rounded.Clear,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ExtractorLeaderButton(onClick = onClick)

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
    }
}


