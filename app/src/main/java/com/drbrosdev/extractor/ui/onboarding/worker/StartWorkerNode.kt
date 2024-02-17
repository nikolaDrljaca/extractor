package com.drbrosdev.extractor.ui.onboarding.worker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.framework.navigation.LocalNavController
import com.drbrosdev.extractor.ui.components.shared.ExtractorActionButton
import com.drbrosdev.extractor.ui.components.shared.OnboardingCard
import com.drbrosdev.extractor.ui.components.shared.OnboardingCardHeadline
import com.drbrosdev.extractor.ui.onboarding.LocalOnbNavController
import com.drbrosdev.extractor.ui.onboarding.OnbNavTarget
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.replaceAll
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object StartWorkerOnbCard : OnbNavTarget {

    @Composable
    override fun Content() {
        val viewModel = koinViewModel<StartWorkerViewModel>()
        val navController = LocalOnbNavController.current
        val rootNavController = LocalNavController.current

        StartWorker(
            onClick = {
                viewModel.spawnWorkRequest()
                viewModel.finishOnboarding()
                rootNavController.replaceAll(ExtractorSearchNavTarget())
            },
            onBack = { navController.pop() }
        )
    }
}

@Composable
private fun StartWorker(
    onClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    OnboardingCard(
        body = stringResource(id = R.string.onb_start),
        headline = {
            OnboardingCardHeadline(
                headline = "Start Extracting!",
                onBack = onBack
            )
        },
        actionButton = {
            ExtractorActionButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Begin")
            }
        }
    )
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        StartWorker(onClick = {}, onBack = { /*TODO*/ })
    }
}