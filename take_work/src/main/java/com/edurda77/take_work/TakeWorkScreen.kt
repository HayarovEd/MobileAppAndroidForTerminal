package com.edurda77.take_work

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.OnScan
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.uikit.rememberRfid
import by.liscode.resources.utils.applySettings
import by.liscode.resources.utils.processScan
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TakeWorkScreenRoot(
    viewModel: TakeWorkScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TakeWorkScreenScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun TakeWorkScreenScreen(
    modifier: Modifier = Modifier,
    state: TakeWorkScreenState,
    onAction: (TakeWorkScreenAction) -> Unit,
) {
    val context = LocalContext.current
    val scanner =  rememberRfid().apply { init(context) }
    var isActive by remember { mutableStateOf(false) }
    LaunchedEffect(state.settings) {
        state.settings?.let {
            scanner.applySettings(it)
        }
    }

    OnScan(
        block = {
            processScan(
                rfid = scanner,
                isActive = isActive,
                block = {
                    Log.d("REST TERMINAL", "result $it")
                },
                updateIsActive = {
                    isActive = it
                }
            )
        }
    )

    UiBaseScaffold(
        message = state.message
    ) {
            paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(15.dp)

        ) {
            Text(
                text = "TakeWorkScreen", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MobileAppAndroidForTerminalTheme {
        TakeWorkScreenScreen(
            state = TakeWorkScreenState(),
            onAction = {}
        )
    }
}