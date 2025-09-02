package by.liscode.fund

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.R
import by.liscode.resources.model.NavigationRoute
import by.liscode.resources.model.fundRoutes
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FundScreenRoot(
    viewModel: FundScreenViewModel = koinViewModel(),
    onNavigateTo: (NavigationRoute) -> Unit,
    bottomRoutes: @Composable () -> Unit,
) {
    BackHandler {}
    val state by viewModel.state.collectAsStateWithLifecycle()
    FundScreenScreen(
        state = state,
        onNavigateTo = onNavigateTo,
        bottomRoutes = bottomRoutes
    )
}

@Composable
fun FundScreenScreen(
    modifier: Modifier = Modifier,
    state: FundScreenState,
    onNavigateTo: (NavigationRoute) -> Unit,
    bottomRoutes: @Composable () -> Unit,
) {
    UiBaseScaffold(
        message = null,
        topBarContent = {
            Row(
                modifier = modifier
                    .statusBarsPadding()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = modifier,
                    text = stringResource(id = R.string.fund),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        },
        bottomBarContent = bottomRoutes
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            state.routes.forEach { route->
                OutlinedButton(
                    modifier = modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                        onNavigateTo(route.route)
                    }
                ) {
                    Text(
                        modifier = modifier,
                        text = stringResource(id = route.nameResId),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Preview(
    showSystemUi = true
)
@Composable
private fun Preview() {
    MobileAppAndroidForTerminalTheme {
        FundScreenScreen(
            state = FundScreenState(
                routes = fundRoutes
            ),
            bottomRoutes = {},
            onNavigateTo = {}
        )
    }
}