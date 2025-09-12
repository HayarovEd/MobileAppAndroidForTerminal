package ru.modulmoney.write_off

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun WriteOffScreenRoot(
    viewModel: WriteOffScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    WriteOffScreenScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun WriteOffScreenScreen(
    modifier: Modifier = Modifier,
    state: WriteOffScreenState,
    onAction: (WriteOffScreenAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    MobileAppAndroidForTerminalTheme {
        WriteOffScreenScreen(
            state = WriteOffScreenState(),
            onAction = {}
        )
    }
}