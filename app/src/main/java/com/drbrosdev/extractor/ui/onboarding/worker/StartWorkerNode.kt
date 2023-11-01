package com.drbrosdev.extractor.ui.onboarding.worker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.ExtractorActionButton
import com.drbrosdev.extractor.ui.components.shared.OnboardingCard
import com.drbrosdev.extractor.ui.components.shared.OnboardingCardHeadline
import com.drbrosdev.extractor.ui.home.HomeNavTarget
import com.drbrosdev.extractor.ui.onboarding.LocalOnbNavController
import com.drbrosdev.extractor.ui.onboarding.OnbNavTarget
import com.drbrosdev.extractor.util.LocalNavController
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

        OnboardingCard(
            body = stringResource(id = R.string.lorem),
            headline = {
                OnboardingCardHeadline(
                    headline = "Start Extracting!",
                    onBack = { navController.pop() }
                )
            },
            actionButton = {
                ExtractorActionButton(
                    onClick = {
                        viewModel.spawnWorkRequest()
                        viewModel.finishOnboarding()
                        rootNavController.replaceAll(HomeNavTarget)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Begin")
                }
            }
        )
    }
}