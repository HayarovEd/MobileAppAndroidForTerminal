package by.liscode.regisration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.InputRow
import by.liscode.resources.uikit.ObserveAsEvents
import by.liscode.resources.uikit.UiIconButton
import by.liscode.resources.uikit.UiOutlandedButton
import by.liscode.resources.uikit.UiTextField
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegistrationScreenRoot(
    viewModel: RegistrationScreenViewModel = koinViewModel(),
    onNavigateToLogin: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snakeBarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    ObserveAsEvents(viewModel.eventFlow) { event ->
        when (event) {
            UiRegestrationEvents.SuccessRegistrationTerminal ->  snakeBarHostState.showSnackbar(
                resources.getString(R.string.success_registration_terminal)
            )
        }
    }

    RegistrationScreenScreen(
        state = state,
        onAction = viewModel::onAction,
        snakeBarHostState = snakeBarHostState,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegistrationScreenScreen(
    modifier: Modifier = Modifier,
    state: RegistrationScreenState,
    snakeBarHostState: SnackbarHostState,
    onAction: (RegistrationScreenAction) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snakeBarHostState) },
        topBar = {
            Row(
                modifier = modifier
                    .statusBarsPadding()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UiIconButton(
                    icon = ImageVector.vectorResource(R.drawable.arrow_back_24dp),
                    onClick = onNavigateToLogin,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    modifier = modifier,
                    text = stringResource(id = R.string.registration_terminal),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(15.dp)
        ) {
            Text(
                modifier = modifier,
                text = stringResource(R.string.api_url),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UiTextField(
                    modifier.weight(6f),
                    content = state.url,
                    singleLine = true,
                    maxLines = 1,
                    onClickContent ={
                        onAction(RegistrationScreenAction.UpdateUrl(it))
                    }
                )
                UiIconButton(
                    modifier.weight(1f),
                    buttonColor = MaterialTheme.colorScheme.primary,
                    color = MaterialTheme.colorScheme.onPrimary,
                    icon = ImageVector.vectorResource(R.drawable.outline_check_24),
                    onClick = { onAction(RegistrationScreenAction.SaveUrl) }
                )
            }
            Spacer(modifier = modifier.height(10.dp))
            InputRow(
                title = "${stringResource(R.string.login_client)}:",
                content = state.login,
                onChangeContent = {
                    onAction(RegistrationScreenAction.UpdateLogin(it))
                },
            )
            InputRow(
                title = "${stringResource(R.string.password_client)}:",
                content = state.password,
                onChangeContent = {
                    onAction(RegistrationScreenAction.UpdatePassword(it))
                },
            )
            Spacer(modifier = modifier.height(10.dp))
            UiOutlandedButton(
                title = stringResource(id = R.string.regestered_terminal),
                enabled =  state.login.isNotBlank()&&state.password.isNotBlank(),
                onClick = {
                    onAction(RegistrationScreenAction.RegisterTerminal)
                }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    val snackBarState = remember { SnackbarHostState() }
    MobileAppAndroidForTerminalTheme {
        RegistrationScreenScreen(
            state = RegistrationScreenState(),
            snakeBarHostState = snackBarState,
            onAction = {},
            onNavigateToLogin = {}
        )
    }
}