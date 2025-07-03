package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@Composable
fun OnboardingContent(
    modifier: Modifier = Modifier,
    painter: @Composable () -> Unit = {},
    topBar: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxSize()
            .then(modifier),
        constraintSet = onboardingCardConstraints()
    ) {
        Row(
            modifier = Modifier
                .layoutId(ViewIds.TOP_BAR)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            topBar?.invoke(this)
        }

        Box(
            modifier = Modifier.layoutId(ViewIds.PAINTER),
            contentAlignment = Alignment.Center
        ) {
            painter()
        }

        Column(
            modifier = Modifier
                .layoutId(ViewIds.CONTENT)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

private fun onboardingCardConstraints() = ConstraintSet {
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val painter = createRefFor(ViewIds.PAINTER)
    val content = createRefFor(ViewIds.CONTENT)

    val topGuideline = createGuidelineFromTop(0.1f)
    val contentGuideline = createGuidelineFromTop(0.5f)

    constrain(topBar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
        bottom.linkTo(topGuideline)
    }

    constrain(painter) {
        top.linkTo(topBar.bottom)
        bottom.linkTo(contentGuideline)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
        height = Dimension.percent(0.35f)
    }

    constrain(content) {
        top.linkTo(painter.bottom)
        bottom.linkTo(parent.bottom)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val TOP_BAR = "topBarBar"
    const val PAINTER = "painter"
    const val CONTENT = "content"
}

@CombinedPreview
@Composable
private fun OnboardingCardPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            Column {
                OnboardingContent(
                    painter = {
                        Image(
                            painter = painterResource(id = R.drawable.ilu_permissions),
                            contentDescription = stringResource(R.string.permission),
                        )
                    },
                    topBar = {
                        InfoIconButton(onClick = {  })
                    },
                ) {
                    Text(text = stringResource(id = R.string.permission))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.function_permission),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}