package com.drbrosdev.extractor

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.parallax.BackstackParallax
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.components.backstack.ui.stack3d.BackStack3D
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.integrationpoint.ActivityIntegrationPoint
import com.bumble.appyx.navigation.integrationpoint.IntegrationPoint
import com.bumble.appyx.navigation.integrationpoint.IntegrationPointProvider
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import kotlinx.parcelize.Parcelize


class RootNode(
    buildContext: BuildContext,
    private val backstack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTarget = NavTarget.SearchRoute,
            savedStateMap = buildContext.savedStateMap
        ),
        motionController = { BackStackSlider(it) }
    )
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = backstack
) {

    @Composable
    override fun View(modifier: Modifier) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppyxComponent(appyxComponent = backstack)
        }
    }

    override fun resolve(interactionTarget: NavTarget, buildContext: BuildContext): Node {
        return when (interactionTarget) {
            NavTarget.PagerRoute -> PagerNode(buildContext, backstack)
            NavTarget.SearchRoute -> SearchNode(buildContext, backstack)
        }
    }
}

class SearchNode(
    buildContext: BuildContext,
    private val backstack: BackStack<NavTarget>
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        val activity = LocalContext.current.findActivity()

        SearchScreen(
            onOpenAppSettings = { activity.openAppSettings() },
            onNavigateToPager = { backstack.push(NavTarget.PagerRoute) }
        )
    }
}

class PagerNode(
    buildContext: BuildContext,
    private val backstack: BackStack<NavTarget>
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        PagerScreen {
            backstack.pop()
        }
    }
}

sealed class NavTarget : Parcelable {
    @Parcelize
    object SearchRoute : NavTarget()

    @Parcelize
    object PagerRoute : NavTarget()
}


open class ComponentNodeActivity : ComponentActivity(), IntegrationPointProvider {
    override lateinit var appyxV2IntegrationPoint: ActivityIntegrationPoint
        protected set

    protected open fun createIntegrationPoint(savedInstanceState: Bundle?) =
        ActivityIntegrationPoint(
            activity = this,
            savedInstanceState = savedInstanceState
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appyxV2IntegrationPoint = createIntegrationPoint(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        appyxV2IntegrationPoint.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        appyxV2IntegrationPoint.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        appyxV2IntegrationPoint.onSaveInstanceState(outState)
    }

}