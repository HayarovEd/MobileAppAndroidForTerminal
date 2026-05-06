package by.liscode.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.ObserveAsEvents
import by.liscode.resources.uikit.UiButton
import by.liscode.resources.uikit.UiTextField
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreenRoot(
    viewModel: LoginScreenViewModel = koinViewModel(),
    onNavigateToFundScreen: () -> Unit,
    onNavigateToRegisterScreen: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snakeBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var isShowDropDownMenu by retain { mutableStateOf(false) }


    ObserveAsEvents(viewModel.eventFlow) { event ->
        when (event) {
            UiLoginEvents.FundNavigateEvent -> onNavigateToFundScreen()
            UiLoginEvents.RegisterNavigateEvent -> onNavigateToRegisterScreen()
            is UiLoginEvents.OnMessage -> snakeBarHostState.showSnackbar(
                event.message.asString(
                    context
                )
            )
        }
    }
    LoginScreenScreen(
        state = state,
        snakeBarHostState = snakeBarHostState,
        isShowDropDownMenu = isShowDropDownMenu,
        onAction = viewModel::onAction,
        updateIsShowDropDownMenu = {
            isShowDropDownMenu = it
        }
    )
}

@Composable
private fun LoginScreenScreen(
    modifier: Modifier = Modifier,
    state: LoginScreenState,
    isShowDropDownMenu: Boolean,
    snakeBarHostState: SnackbarHostState,
    onAction: (LoginScreenAction) -> Unit,
    updateIsShowDropDownMenu: (Boolean) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snakeBarHostState) },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = modifier.clickable {
                    onAction(LoginScreenAction.UpdateCounter)
                },
                painter = painterResource(R.drawable.logo_terminal),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(id = R.string.login),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Row(
                    modifier = Modifier
                        .clickable {
                            updateIsShowDropDownMenu(true)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = modifier.fillMaxWidth(0.4f),
                        text = state.selectedEmployee?.name?:"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.arrow_drop_down_24dp),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DropdownMenu(
                        expanded = isShowDropDownMenu,
                        onDismissRequest = {
                            updateIsShowDropDownMenu(false)
                        }
                    ) {
                        state.employees.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = it.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                onClick = {
                                    updateIsShowDropDownMenu(false)
                                    onAction(LoginScreenAction.UpdateSelectedEmployee(it))
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                modifier = modifier.align(Alignment.Start),
                text = stringResource(id = R.string.password),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = modifier.height(5.dp))
            UiTextField(
                content = state.password,
                enabled = !state.isLoading,
                imeAction = ImeAction.Done,
                singleLine = true,
                onClickContent = { onAction(LoginScreenAction.UpdatePassword(it)) },
                placeholder = stringResource(id = R.string.enter_password)
            )
            Spacer(modifier = Modifier.height(25.dp))
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                UiButton(
                    title = stringResource(id = R.string.enter),
                    enabled = state.selectedEmployee != null && state.password.isNotEmpty() && state.typeTerminalBearer == TypeTerminalBearer.OK,
                    onClick = {
                        if (!state.isLoading) {
                            onAction(
                                LoginScreenAction.OnLogin
                            )
                        }
                    },
                )
            }
            if (state.typeTerminalBearer == TypeTerminalBearer.EMPTY) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    modifier = modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(id = R.string.terminal_token_empty),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (state.typeTerminalBearer == TypeTerminalBearer.EXPIRED) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    modifier = modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(id = R.string.terminal_token_expired),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MobileAppAndroidForTerminalTheme {
        val snakeBarHostState = remember { SnackbarHostState() }
        LoginScreenScreen(
            state = LoginScreenState(),
            isShowDropDownMenu = false,
            snakeBarHostState = snakeBarHostState,
            onAction = {},
            updateIsShowDropDownMenu = {}
        )
    }
}

@Preview
@Composable
private fun Preview2() {
    MobileAppAndroidForTerminalTheme {
        val snakeBarHostState = remember { SnackbarHostState() }
        LoginScreenScreen(
            state = LoginScreenState(
                typeTerminalBearer = TypeTerminalBearer.EXPIRED,
                password = "123456"
            ),
            isShowDropDownMenu = false,
            snakeBarHostState = snakeBarHostState,
            onAction = {},
            updateIsShowDropDownMenu = {}
        )
    }
}

@Preview
@Composable
private fun Preview3() {
    MobileAppAndroidForTerminalTheme {
        val snakeBarHostState = remember { SnackbarHostState() }
        LoginScreenScreen(
            state = LoginScreenState(
                password = "123456",
                isLoading = true,
                typeTerminalBearer = TypeTerminalBearer.EMPTY,
            ),
            isShowDropDownMenu = true,
            snakeBarHostState = snakeBarHostState,
            onAction = {},
            updateIsShowDropDownMenu = {}
        )
    }
}