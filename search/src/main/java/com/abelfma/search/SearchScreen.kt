package com.abelfma.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreenRoot(
    viewModel: SearchScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SearchScreenScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun SearchScreenScreen(
    modifier: Modifier = Modifier,
    state: SearchScreenState,
    onAction: (SearchScreenAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    MobileAppAndroidForTerminalTheme {
        SearchScreenScreen(
            state = SearchScreenState(),
            onAction = {}
        )
    }
}