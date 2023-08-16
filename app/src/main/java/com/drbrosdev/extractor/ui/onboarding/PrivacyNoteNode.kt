package com.drbrosdev.extractor.ui.onboarding

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

class PrivacyNoteNode(
    buildContext: BuildContext,
    private val onBackPressed: () -> Unit
) : Node(buildContext) {
    @Composable
    override fun View(modifier: Modifier) {
        OnboardingCard(
            body = stringResource(id = R.string.lorem),
            headline = {
                OnboardingCardHeadline(
                    headline = "A Note on Privacy",
                    onBack = {
                        onBackPressed()
                    }
                )

            },
            actionButton = {
                Button(onClick = {
                    //do something
                    finish()
                }) {
                    Text(text = "Understood!")
                }
            }
        )
    }
}