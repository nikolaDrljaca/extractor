package com.drbrosdev.extractor.ui.settings.bug

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.EmbeddingTextField
import com.drbrosdev.extractor.ui.components.shared.ExtractorButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorDialog
import com.drbrosdev.extractor.ui.components.shared.InfoIconButton


@Composable
fun ExtractorFeedbackScreen(
    onBack: () -> Unit,
    onSubmit: () -> Unit,
    state: ExtractorFeedbackState
) {
    val textStyle = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Normal
    )

    val smallLabel = MaterialTheme.typography.labelSmall.copy(
        color = Color.Gray
    )

    var shouldShowInfoDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (shouldShowInfoDialog) {
        ExtractorDialog(
            onAction = {
                shouldShowInfoDialog = !shouldShowInfoDialog
            },
            header = {
                Image(
                    painter = painterResource(id = R.drawable.ilu_info),
                    contentDescription = "Info graphic",
                    modifier = Modifier.size(164.dp)
                )
            }
        ) {
            Text(
                text = stringResource(R.string.event_log_dialog),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
        constraintSet = bugReportScreenConstraintSet()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .layoutId(ViewIds.TOP_BAR),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BackIconButton(
                onBack = onBack,
                modifier = Modifier.layoutId(ViewIds.TOP_BAR)
            )
            InfoIconButton(onClick = { shouldShowInfoDialog = true })
        }

        Column(
            modifier = Modifier
                .layoutId(ViewIds.BUG_FORM),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.send_us_feedback),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.submit_feedback_header),
                style = textStyle
            )

            Spacer(modifier = Modifier.height(8.dp))

            EmbeddingTextField(
                value = state.userText,
                onTextChange = state::onUserTextChanged,
                label = {
                    Text(text = stringResource(R.string.what_you_think))
                },
                enabled = state.enabled
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Checkbox(
                    checked = state.includeEventLogs,
                    onCheckedChange = state::onIncludeEventLogsChanged,
                    enabled = state.enabled
                )

                Text(text = stringResource(R.string.include_event_logs))
            }
        }

        Text(
            text = stringResource(R.string.event_log_disclaimer),
            modifier = Modifier.layoutId(ViewIds.DISCLAIMER),
            style = smallLabel
        )

        ExtractorButton(
            onClick = onSubmit,
            modifier = Modifier.layoutId(ViewIds.SUBMIT),
            enabled = state.enabled
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeCap = StrokeCap.Round,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = stringResource(R.string.submit))
            }
        }
    }
}

private fun bugReportScreenConstraintSet() = ConstraintSet {
    val topBarRef = createRefFor(ViewIds.TOP_BAR)
    val formRef = createRefFor(ViewIds.BUG_FORM)
    val submit = createRefFor(ViewIds.SUBMIT)
    val disclaimer = createRefFor(ViewIds.DISCLAIMER)

    val topGuideline = createGuidelineFromTop(0.13f)

    constrain(topBarRef) {
        start.linkTo(parent.start)
        top.linkTo(parent.top, margin = 8.dp)
    }

    constrain(submit) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        bottom.linkTo(parent.bottom, margin = 12.dp)
        width = Dimension.fillToConstraints
    }

    constrain(formRef) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        top.linkTo(topGuideline)
        width = Dimension.fillToConstraints
    }

    constrain(disclaimer) {
        bottom.linkTo(submit.top, margin = 10.dp)
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val TOP_BAR = "topBar"
    const val BUG_FORM = "bugForm"
    const val SUBMIT = "submit"
    const val DISCLAIMER = "disclaimer"
}
