package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.ExtractorActionButton
import com.drbrosdev.extractor.ui.components.OnboardingCard
import com.drbrosdev.extractor.ui.components.OnboardingCardHeadline

class WelcomeNode(
    buildContext: BuildContext,
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {

        OnboardingCard(
            body = stringResource(id = R.string.lorem),
            headline = {
                OnboardingCardHeadline(headline = "What is <app name>?")
            },
            actionButton = {
                ExtractorActionButton(
                    onClick = { finish() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Let's get started!")
                }
            },
            modifier = modifier
        )
    }
}