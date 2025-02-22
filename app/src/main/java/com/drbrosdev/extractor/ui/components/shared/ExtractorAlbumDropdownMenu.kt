package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.drbrosdev.extractor.R

sealed interface ExtractorDropdownAction {

    data object Delete : ExtractorDropdownAction

    data object Share : ExtractorDropdownAction
}

@Composable
fun ExtractorAlbumDropdownMenu(
    modifier: Modifier = Modifier,
    onAction: (ExtractorDropdownAction) -> Unit
) {
    var isDropdownOpen by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
            .then(modifier)
    ) {

        IconButton(onClick = { isDropdownOpen = true }) {
//            Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "")
        }

        DropdownMenu(
            expanded = isDropdownOpen,
            onDismissRequest = { isDropdownOpen = false },
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.dropdown_share_all)) },
                onClick = {
                    onAction(ExtractorDropdownAction.Share)
                    isDropdownOpen = false
                },
                leadingIcon = { Icon(imageVector = Icons.Rounded.Share, contentDescription = stringResource(R.string.dropdown_share_all)) }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.dropdown_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    onAction(ExtractorDropdownAction.Delete)
                    isDropdownOpen = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.dropdown_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }
}
