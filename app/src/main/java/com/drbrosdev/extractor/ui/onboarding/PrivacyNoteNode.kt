package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.ExtractorActionButton
import com.drbrosdev.extractor.ui.components.OnboardingCard
import com.drbrosdev.extractor.ui.components.OnboardingCardHeadline
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize

@Parcelize
object PrivacyNoteOnbCard : OnbNavTarget {

    @Composable
    override fun Content() {
        val navController = LocalOnbNavController.current

        OnboardingCard(
            body = stringResource(id = R.string.lorem),
            headline = {
                OnboardingCardHeadline(
                    headline = "A Note on Privacy",
                    onBack = { navController.pop() }
                )

            },
            actionButton = {
                ExtractorActionButton(
                    onClick = { navController.navigate(PermissionOnbCard) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Understood!")
                }
            }
        )
    }
}