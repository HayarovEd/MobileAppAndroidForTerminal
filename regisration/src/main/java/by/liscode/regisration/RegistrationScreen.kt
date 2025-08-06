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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.UiBaseScaffold
import org.koin.androidx.compose.koinViewModel
import by.liscode.resources.R
import by.liscode.resources.uikit.InputRow
import by.liscode.resources.uikit.UiIconButton

@Composable
fun RegistrationScreenRoot(
    viewModel: RegistrationScreenViewModel = koinViewModel(),
    onNavigateToLogin: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RegistrationScreenScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegistrationScreenScreen(
    modifier: Modifier = Modifier,
    state: RegistrationScreenState,
    onAction: (RegistrationScreenAction) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    UiBaseScaffold(
        message = state.message,
        topBarContent = {
            Row(
                modifier = modifier
                    .statusBarsPadding()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UiIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
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
            InputRow(
                title = stringResource(R.string.api_url),
                content = state.url,
                trailingIcon = ImageVector.vectorResource(R.drawable.outline_check_24),
                onChangeContent = {
                    onAction(RegistrationScreenAction.UpdateUrl(it))
                },
                onClickTrailingIcon = {
                    onAction(RegistrationScreenAction.SaveUrl)
                }
            )
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
            OutlinedButton(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(5.dp),
                enabled = state.login.isNotBlank()&&state.password.isNotBlank(),
                onClick = {
                    onAction(RegistrationScreenAction.RegisterTerminal)
                }
            ) {
                Text(
                    modifier = modifier,
                    text = stringResource(id = R.string.regestered_terminal),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
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
        RegistrationScreenScreen(
            state = RegistrationScreenState(),
            onAction = {},
            onNavigateToLogin = {}
        )
    }
}