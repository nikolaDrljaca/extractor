package com.drbrosdev.extractor.ui.imageinfo.edit

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.shared.AppImageInfoHeader
import com.drbrosdev.extractor.ui.imageinfo.LupaImageHeaderState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import kotlinx.parcelize.Parcelize


@Parcelize
data class EditLupaAnnotationsNavTarget(
    val mediaImageId: Long
) : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        TODO("Not yet implemented")
    }
}


@Immutable
data class AnnotationListState(
    val items: List<String>,
    val onDelete: (annotationValue: String) -> Unit
)

@Composable
fun EditLupaAnnotationsScreen(
    modifier: Modifier = Modifier,
    headerState: LupaImageHeaderState,
    annotations: AnnotationListState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item(key = "header") {
            AppImageInfoHeader(
                model = headerState,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item { Spacer(Modifier.height(24.dp)) }

        itemsIndexed(annotations.items) { index, item ->
            val position = remember {
                when (index) {
                    0 -> ItemPosition.FIRST
                    annotations.items.lastIndex -> ItemPosition.LAST
                    else -> ItemPosition.MIDDLE
                }
            }
            AnnotationListItem(
                value = item,
                position = position,
                onDelete = {}
            )
        }
    }
}

enum class ItemPosition {
    FIRST, MIDDLE, LAST
}

@Composable
fun AnnotationListItem(
    modifier: Modifier = Modifier,
    value: String,
    position: ItemPosition,
    onDelete: (annotationValue: String) -> Unit
) {
    // TODO Performance?
    val shape = when(position) {
        ItemPosition.FIRST -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 2.dp, bottomEnd = 2.dp)
        ItemPosition.LAST -> RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
        ItemPosition.MIDDLE -> RoundedCornerShape(2.dp)
    }
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> onDelete(value)
                SwipeToDismissBoxValue.EndToStart -> onDelete(value)
                SwipeToDismissBoxValue.Settled -> Unit
            }
            it != SwipeToDismissBoxValue.StartToEnd
        }
    )
    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = modifier,
        backgroundContent = {
            when (swipeToDismissBoxState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "delete",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.error)
                            .wrapContentSize(Alignment.CenterStart)
                            .padding(12.dp)
                            .clip(shape),
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "delete",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.error)
                            .wrapContentSize(Alignment.CenterEnd)
                            .padding(12.dp)
                            .clip(shape),
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
                SwipeToDismissBoxValue.Settled -> Unit
            }
        }
    ) {
        ListItem(
            headlineContent = { Text(text = value) },
            modifier = Modifier
                .padding(bottom = 2.dp)
                .clip(shape),
            tonalElevation = 4.dp
        )
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            EditLupaAnnotationsScreen(
                headerState = LupaImageHeaderState(
                    mediaImageId = 12123123,
                    uri = Uri.EMPTY.toString(),
                    dateAdded = "2025-01-01"
                ),
                annotations = AnnotationListState(
                    items = buildList {
                        repeat(12) { add("Item $it") }
                    },
                    onDelete = {}
                )
            )
        }
    }
}