package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.ExtractorActionButton
import com.drbrosdev.extractor.ui.components.shared.OnboardingCard
import com.drbrosdev.extractor.ui.components.shared.OnboardingCardHeadline
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize

@Parcelize
object PrivacyNoteOnbCard : OnbNavTarget {

    @Composable
    override fun Content() {
        val navController = LocalOnbNavController.current

        PrivacyNoteCard(
            onClick = { navController.navigate(PermissionOnbCard) },
            onBack = { navController.pop() }
        )
    }
}

@Composable
private fun PrivacyNoteCard(
    onClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    OnboardingCard(
        body = stringResource(id = R.string.onb_privacy),
        headline = {
            OnboardingCardHeadline(
                headline = stringResource(R.string.a_note_on_privacy),
                onBack = onBack
            )
        },
        actionButton = {
            ExtractorActionButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.understood))
            }
        },
        modifier = Modifier.zIndex(1f)
    )
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        PrivacyNoteCard(onClick = { /*TODO*/ }, onBack = { /*TODO*/ })
    }
}