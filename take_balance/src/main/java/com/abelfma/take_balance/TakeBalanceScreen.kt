package com.abelfma.take_balance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun TakaBalanceScreenRoot(
    viewModel: TakeBalanceScreenViewModel = koinViewModel ()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TakaBalanceScreenScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun TakaBalanceScreenScreen(
    modifier: Modifier = Modifier,
    state: TakeBalanceScreenState,
    onAction: (TakeBalanceScreenAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    MobileAppAndroidForTerminalTheme {
        TakaBalanceScreenScreen(
            state = TakeBalanceScreenState(),
            onAction = {}
        )
    }
}