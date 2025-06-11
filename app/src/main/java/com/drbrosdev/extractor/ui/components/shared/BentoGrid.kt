package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@Composable
fun BentoGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    spacedBy: Dp = 0.dp,
    columns: Int,
    content: BentoGridScope.() -> Unit
) {
    val scope = remember(content) {
        BentoGridScope().apply(content)._items
    }

    Layout(
        content = {
            scope.forEach { item ->
                item.content()
            }
        },
        modifier = modifier
    ) { measurables, constraints ->
        // content padding
        val paddingLeft = contentPadding.calculateLeftPadding(layoutDirection).roundToPx()
        val paddingRight = contentPadding.calculateRightPadding(layoutDirection).roundToPx()
        val paddingTop = contentPadding.calculateTopPadding().roundToPx()
        val paddingBottom = contentPadding.calculateBottomPadding().roundToPx()

        val spacing = spacedBy.roundToPx()

        val layoutData = calculateGridLayout(
            measurables = measurables,
            columns = columns,
            maxWidth = constraints.maxWidth,
            paddingLeft = paddingLeft,
            paddingRight = paddingRight,
            paddingTop = paddingTop,
            paddingBottom = paddingBottom,
            hSpacing = spacing,
            vSpacing = spacing,
            items = scope
        )

        with(layoutData) {
            layout(width, height) {
                placeables.forEachIndexed { i, placeable ->
                    val (x, y) = positions[i]
                    placeable.place(x, y)
                }
            }
        }
    }
}

enum class BentoStyle {
    ONE,
    TWO,
    THREE
}

class BentoGridScope {
    internal val _items = mutableListOf<BentoGridItem>()

    fun item(spanX: Int, spanY: Int, content: @Composable () -> Unit) {
        _items += BentoGridItem(spanX, spanY, content)
    }

    fun item(content: @Composable () -> Unit) {
        _items += BentoGridItem(spanY = 1, spanX = 1, content = content)
    }

    fun <T> items(
        values: List<T>,
        content: @Composable (T) -> Unit
    ) {
        val style = BentoStyle.entries.toTypedArray().random()
        values.forEachIndexed { index, item ->
            val gridSize = determineGridSize(index, style)
            if (gridSize != null) {
                val (x, y) = gridSize
                item(spanY = y, spanX = x) { content(item) }
            }
        }
    }

    fun <T> items(
        values: List<T>,
        style: BentoStyle,
        content: @Composable (index: Int, item: T) -> Unit
    ) {
        values.forEachIndexed { index, item ->
            val gridSize = determineGridSize(index, style)
            if (gridSize != null) {
                val (x, y) = gridSize
                item(spanY = y, spanX = x) { content(index, item) }
            }
        }
    }

    private fun determineGridSize(
        index: Int,
        style: BentoStyle
    ): Pair<Int, Int>? {
        return when (style) {
            BentoStyle.ONE -> layoutOne(index)
            BentoStyle.TWO -> layoutTwo(index)
            BentoStyle.THREE -> layoutThree(index)
        }
    }

    // predetermined layouts
    private fun layoutOne(index: Int): Pair<Int, Int>? {
        return when (index) {
            0 -> 1 to 1
            1 -> 1 to 1
            2 -> 1 to 2
            3 -> 2 to 2
            4 -> 2 to 2
            5 -> 1 to 1
            6 -> 1 to 1
            7 -> 1 to 1
            else -> null
        }
    }

    private fun layoutTwo(index: Int): Pair<Int, Int>? {
        return when (index) {
            0 -> 1 to 2
            1 -> 1 to 1
            2 -> 1 to 1
            3 -> 1 to 1
            4 -> 1 to 2
            5 -> 1 to 1
            6 -> 2 to 2
            7 -> 1 to 1
            8 -> 1 to 1
            9 -> 1 to 1
            else -> null
        }
    }

    private fun layoutThree(index: Int): Pair<Int, Int>? {
        return when (index) {
            0 -> 1 to 1
            1 -> 1 to 1
            2 -> 2 to 2
            3 -> 1 to 1
            4 -> 2 to 2
            5 -> 1 to 1
            6 -> 1 to 1
            7 -> 1 to 1
            8 -> 1 to 1
            else -> null
        }
    }
}

data class BentoGridItem(
    val spanX: Int,
    val spanY: Int,
    val content: @Composable () -> Unit
)

private data class GridLayoutData(
    val placeables: List<Placeable>,
    val positions: List<Pair<Int, Int>>,
    val width: Int,
    val height: Int
)

private fun calculateGridLayout(
    measurables: List<Measurable>,
    columns: Int,
    maxWidth: Int,
    paddingLeft: Int,
    paddingRight: Int,
    paddingTop: Int,
    paddingBottom: Int,
    hSpacing: Int,
    vSpacing: Int,
    items: List<BentoGridItem>
): GridLayoutData {
    val availableWidth = maxWidth - paddingLeft - paddingRight - (columns - 1) * hSpacing
    val cellWidth = availableWidth / columns
    val cellHeight = cellWidth // Square cells

    val occupied = mutableSetOf<Pair<Int, Int>>()
    val positions = mutableListOf<Pair<Int, Int>>()
    val placeables = mutableListOf<Placeable>()

    var row = 0
    var col = 0

    fun findNextAvailable(spanX: Int, spanY: Int): Pair<Int, Int> {
        while (true) {
            if ((col + spanX) > columns) {
                col = 0
                row++
                continue
            }

            val fits = (0 until spanX).all { dx ->
                (0 until spanY).all { dy ->
                    (col + dx to row + dy) !in occupied
                }
            }

            if (fits) return col to row
            else {
                col++
                if (col >= columns) {
                    col = 0
                    row++
                }
            }
        }
    }

    items.forEachIndexed { index, item ->
        val (x, y) = findNextAvailable(item.spanX, item.spanY)

        // Mark occupied cells
        for (dx in 0 until item.spanX) {
            for (dy in 0 until item.spanY) {
                occupied += (x + dx to y + dy)
            }
        }

        val width = item.spanX * cellWidth + (item.spanX - 1) * hSpacing
        val height = item.spanY * cellHeight + (item.spanY - 1) * vSpacing

        val placeable = measurables[index].measure(
            Constraints.fixed(width, height)
        )

        val px = paddingLeft + x * (cellWidth + hSpacing)
        val py = paddingTop + y * (cellHeight + vSpacing)

        positions += px to py
        placeables += placeable
    }

    val totalRows = (occupied.maxOfOrNull { it.second } ?: 0) + 1
    val totalHeight =
        paddingTop + totalRows * cellHeight + (totalRows - 1) * vSpacing + paddingBottom
    val totalWidth = maxWidth

    return GridLayoutData(placeables, positions, totalWidth, totalHeight)
}

@Composable
private fun BoxItem(modifier: Modifier = Modifier) {
    AsyncImage(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.LightGray),
        model = ImageRequest.Builder(LocalContext.current)
            .data(R.drawable.baseline_image_24)
            .crossfade(true)
            .build(),
        contentDescription = "Loaded image",
        contentScale = ContentScale.Crop,
    )
}

@Preview
@Composable
private fun CurrentPreview() {
    val data = (0..15).toList()
    ExtractorTheme {
        Surface {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Style 1")
                BentoGrid(
                    columns = 5,
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(data, BentoStyle.ONE) { _, _ -> BoxItem() }
                }
                Text("Style 2")
                BentoGrid(
                    columns = 5,
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(data, BentoStyle.TWO) { _, _ -> BoxItem() }
                }
                Text("Style 3")
                BentoGrid(
                    columns = 5,
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(data, BentoStyle.THREE) { _, _ -> BoxItem() }
                }
            }
        }
    }
}