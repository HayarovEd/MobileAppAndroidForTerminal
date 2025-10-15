package by.liscode.splash

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.UiBaseScaffold
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreenRoot(
    viewModel: SplashScreenViewModel = koinViewModel(),
    onNavigateToFundScreen: () -> Unit,
    onNavigateToLoginScreen: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiSplashEvents.FundNavigateEvent -> onNavigateToFundScreen()
                UiSplashEvents.LoginNavigateEvent -> onNavigateToLoginScreen()
            }
        }
    }

    SplashScreenScreen()
}

@Composable
fun SplashScreenScreen(
    modifier: Modifier = Modifier,
) {
    UiBaseScaffold(
        message = null
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 3f,
                animationSpec = infiniteRepeatable(tween(2000)),
                label = ""
            )
            Image(
                modifier = modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin.Center
                    }
                    .align(Alignment.Center),
                painter = painterResource(R.drawable.logo_terminal),
                contentDescription = ""
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MobileAppAndroidForTerminalTheme {
        SplashScreenScreen()
    }
}