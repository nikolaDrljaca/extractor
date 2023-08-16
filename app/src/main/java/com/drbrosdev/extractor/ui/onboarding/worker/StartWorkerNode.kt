package com.drbrosdev.extractor.ui.onboarding.worker

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.OnboardingCard
import com.drbrosdev.extractor.ui.components.OnboardingCardHeadline
import org.koin.androidx.compose.koinViewModel

class StartWorkerNode(
    buildContext: BuildContext,
    private val onBackPressed: () -> Unit
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        val viewModel = koinViewModel<StartWorkerViewModel>()

        OnboardingCard(
            body = stringResource(id = R.string.lorem),
            headline = {
                OnboardingCardHeadline(
                    headline = "Start Extracting!",
                    onBack = {
                        onBackPressed()
                    }
                )
            },
            actionButton = {
                Button(onClick = {
                    viewModel.spawnWorkRequest()
                    finish()
                }) {
                    Text(text = "Begin")
                }
            }
        )
    }
}