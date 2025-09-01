package by.liscode.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.uikit.UiTextField
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreenRoot(
    viewModel: LoginScreenViewModel = koinViewModel(),
    onNavigateToFundScreen: () -> Unit,
    onNavigateToRegisterScreen: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiLoginEvents.FundNavigateEvent -> onNavigateToFundScreen()
                UiLoginEvents.RegisterNavigateEvent -> onNavigateToRegisterScreen()
            }
        }
    }
    LoginScreenScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun LoginScreenScreen(
    modifier: Modifier = Modifier,
    state: LoginScreenState,
    onAction: (LoginScreenAction) -> Unit,
) {
    UiBaseScaffold(
        message = state.message
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
            Text(
                modifier = modifier.align(Alignment.Start),
                text = stringResource(id = R.string.login),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = modifier.height(5.dp))
            UiTextField(
                content = state.login,
                enabled = !state.isLoading,
                onClickContent = { onAction(LoginScreenAction.UpdateLogin(it)) },
                label = stringResource(id = R.string.enter_login)
            )
            Spacer(modifier = Modifier.height(15.dp))
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
                onClickContent = { onAction(LoginScreenAction.UpdatePassword(it)) },
                label = stringResource(id = R.string.enter_password)
            )
            Spacer(modifier = Modifier.height(25.dp))
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    modifier = modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    enabled = state.login.isNotEmpty() && state.password.isNotEmpty(),
                    onClick = {
                        if (!state.isLoading) {
                            onAction(
                                LoginScreenAction.OnLogin
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.enter),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MobileAppAndroidForTerminalTheme {
        LoginScreenScreen(
            state = LoginScreenState(),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun Preview2() {
    MobileAppAndroidForTerminalTheme {
        LoginScreenScreen(
            state = LoginScreenState(
                login = "gggh",
                password = "123456"
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun Preview3() {
    MobileAppAndroidForTerminalTheme {
        LoginScreenScreen(
            state = LoginScreenState(
                login = "gggh",
                password = "123456",
                isLoading = true,
            ),
            onAction = {}
        )
    }
}