package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorAlbumActionBottomSheet(
    onAction: (ExtractorAlbumBottomSheetAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    ModalBottomSheet(
        onDismissRequest = { onAction(ExtractorAlbumBottomSheetAction.Dismiss) },
        dragHandle = { Spacer(modifier = Modifier.height(12.dp)) },
        modifier = Modifier
            .then(modifier),
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        shape = RoundedCornerShape(14.dp)
    ) {
        ExtractorActionBottomSheetItem(
            onClick = { onAction(ExtractorAlbumBottomSheetAction.Share) },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = "",
                )
            }
        ) {
            Text(
                text = stringResource(id = R.string.dropdown_share_all),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp
                )
            )
        }

        ExtractorActionBottomSheetItem(
            onClick = { onAction(ExtractorAlbumBottomSheetAction.Delete) },
            contentColor = MaterialTheme.colorScheme.error,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "",
                )
            }
        ) {
            Text(
                text = stringResource(id = R.string.dropdown_delete),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp + bottomPadding))
    }
}

sealed interface ExtractorAlbumBottomSheetAction {
    data object Dismiss : ExtractorAlbumBottomSheetAction

    data object Share : ExtractorAlbumBottomSheetAction

    data object Delete : ExtractorAlbumBottomSheetAction
}

@Composable
private fun ExtractorActionBottomSheetItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    icon: (@Composable () -> Unit)? = null,
    text: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        contentColor = contentColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .then(modifier)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.invoke()
            Spacer(modifier = Modifier.width(16.dp))
            text()
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        ExtractorAlbumActionBottomSheet(onAction = {})
    }
}