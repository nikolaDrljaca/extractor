package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.ExtractorActionButton
import com.drbrosdev.extractor.ui.components.shared.OnboardingCard
import com.drbrosdev.extractor.ui.components.shared.OnboardingCardHeadline
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.parcelize.Parcelize

@Parcelize
object WelcomeOnbCard : OnbNavTarget {

    @Composable
    override fun Content() {
        val navController = LocalOnbNavController.current

        OnboardingCard(
            body = stringResource(id = R.string.lorem),
            headline = {
                OnboardingCardHeadline(headline = "What is <app name>?")
            },
            actionButton = {
                ExtractorActionButton(
                    onClick = { navController.navigate(PrivacyNoteOnbCard) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Let's get started!")
                }
            },
            modifier = Modifier.zIndex(2f)
        )
    }
}