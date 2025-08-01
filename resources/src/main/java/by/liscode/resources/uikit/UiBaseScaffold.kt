package by.liscode.resources.uikit

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun UiBaseScaffold(
    modifier: Modifier = Modifier,
    message: UiText?,
    topBarContent: @Composable () -> Unit = {},
    bottomBarContent: @Composable () -> Unit = {},
    fabContent: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val snakeBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(key1 = message) {
        if (message != null) {
            snakeBarHostState.showSnackbar(
                message = message.asString(context),
                duration = SnackbarDuration.Short
            )
        }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.primary,
        snackbarHost = { SnackbarHost(snakeBarHostState) },
        topBar = topBarContent,
        bottomBar = bottomBarContent,
        floatingActionButton = fabContent
    ) { paddings ->
        content(paddings)
    }
}