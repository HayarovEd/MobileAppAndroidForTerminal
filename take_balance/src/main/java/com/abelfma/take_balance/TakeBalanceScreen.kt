package com.abelfma.take_balance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun TakeBalanceScreenRoot(
    viewModel: TakeBalanceScreenViewModel = koinViewModel (),
    onNavigateToLoginScreen: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiTakeBalanceEvents.LoginNavigationEvent -> onNavigateToLoginScreen()
            }
        }
    }
    TakeBalanceScreenScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun TakeBalanceScreenScreen(
    modifier: Modifier = Modifier,
    state: TakeBalanceScreenState,
    onAction: (TakeBalanceScreenAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    MobileAppAndroidForTerminalTheme {
        TakeBalanceScreenScreen(
            state = TakeBalanceScreenState(),
            onAction = {}
        )
    }
}