package com.drbrosdev.extractor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerScreen(
    onBackClick: () -> Unit
) {

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                Text(text = "Pager screen")
                Button(onClick = onBackClick) {
                    Text("Go back")
                }
            }

            val pagerState = rememberPagerState { 4 }

            HorizontalPager(
                state = pagerState,
            ) {
                Text(
                    text = "Page: $it", modifier = Modifier
                        .fillMaxWidth()
                        .height(144.dp)
                )
            }

            Row(
                Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(4) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(20.dp)
                    )
                }
            }
        }
    }
}