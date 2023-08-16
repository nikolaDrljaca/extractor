package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.OnboardingCard

class WelcomeNode(
    buildContext: BuildContext,
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {

        OnboardingCard(
            headline = "What is <app_name>?",
            body = stringResource(id = R.string.lorem),
            actionButton = {
                Button(onClick = {
                    //do things
                    finish()
                }) {
                    Text(text = "Let's get started!")
                }
            },
            modifier = modifier
        )
    }
}