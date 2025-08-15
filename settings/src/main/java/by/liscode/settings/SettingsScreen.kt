package by.liscode.settings

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.domain.model.FrequencyMode
import by.liscode.domain.model.RfidAttribute
import by.liscode.domain.model.RfidProtocol
import by.liscode.domain.model.RfidSetting
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.UiBaseScaffold
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsScreenViewModel = koinViewModel(),
    bottomRoutes: @Composable () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiSettingsEvents.LoginNavigateEvent -> onNavigateToLogin()
            }
        }
    }
    SettingsScreenScreen(
        state = state,
        onAction = viewModel::onAction,
        bottomRoutes = bottomRoutes
    )
}


@SuppressLint("ContextCastToActivity")
@Composable
fun SettingsScreenScreen(
    modifier: Modifier = Modifier,
    state: SettingsScreenState,
    bottomRoutes: @Composable () -> Unit,
    onAction: (SettingsScreenAction) -> Unit,
) {
    val activity = LocalContext.current as? Activity
    var isShowFiltersDialog by remember { mutableStateOf(false) }
    var isExpandedProtocolMenu by remember { mutableStateOf(false) }
    var isExpandedFrequencyMenu by remember { mutableStateOf(false) }

    if (isShowFiltersDialog) {
        Dialog(
            onDismissRequest = {
                isShowFiltersDialog = false
            }
        ) {
            FiltersDialog(
                filters = state.filters,
                value = state.query,
                label = stringResource(R.string.mask),
                onConfirm = {
                    onAction(SettingsScreenAction.InsertFilter)
                },
                onChangeValue = {
                    onAction(SettingsScreenAction.UpdateQuery(it))
                },
                onClear = {
                    onAction(SettingsScreenAction.DeleteFilters)
                },
                onClose = {
                    isShowFiltersDialog = false
                }
            )
        }
    }


    UiBaseScaffold(
        message = state.message,
        topBarContent = {
            Row(
                modifier = modifier
                    .statusBarsPadding()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = modifier,
                    text = stringResource(id = R.string.settings),
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
                .padding(15.dp)
        ) {
            OutlinedButton(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(5.dp),
                onClick = {
                    isShowFiltersDialog = true
                }
            ) {
                Text(
                    modifier = modifier,
                    text = stringResource(id = R.string.setting_filter),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = modifier.height(10.dp))
            state.rfidSetting?.let { settings->
                Text(
                    modifier = modifier,
                    text = "${stringResource(id = R.string.power) }: ${(settings.power).roundToInt()}",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = modifier.height(10.dp))
                Slider(
                    modifier = modifier.fillMaxWidth(),
                    value = settings.power,
                    valueRange = 1f..30f,
                    steps = 29,
                    onValueChange = {
                        onAction(SettingsScreenAction.UpdatePower(it))
                    },
                    onValueChangeFinished = {
                        onAction(SettingsScreenAction.CompleteUpdatePower)
                    }
                )
                Spacer(modifier = modifier.height(10.dp))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = modifier,
                        text = "${stringResource(id = R.string.protocol)}:",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = modifier.width(5.dp))
                    Text(
                        modifier = modifier.clickable {
                            isExpandedProtocolMenu = true
                        },
                        text = settings.protocol.title,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    DropdownMenu(
                        expanded = isExpandedProtocolMenu,
                        onDismissRequest = {
                            isExpandedProtocolMenu = false
                        }
                    ) {
                        RfidProtocol.entries.forEach {
                            DropdownMenuItem(
                                onClick = {
                                    onAction(SettingsScreenAction.UpdateStandard(it))
                                },
                                text = {
                                    Text(
                                        text = it.title,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = modifier.height(10.dp))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = modifier,
                        text = "${stringResource(id = R.string.frequency)}:",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = modifier.width(5.dp))
                    Text(
                        modifier = modifier.clickable {
                            isExpandedFrequencyMenu = true
                        },
                        text = settings.frequencyMode.title,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    DropdownMenu(
                        expanded = isExpandedFrequencyMenu,
                        onDismissRequest = {
                            isExpandedFrequencyMenu = false
                        }
                    ) {
                        FrequencyMode.entries.forEach {
                            DropdownMenuItem(
                                onClick = {
                                    onAction(SettingsScreenAction.UpdateFrequencyMode(it))
                                },
                                text = {
                                    Text(
                                        text = it.title,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = modifier.height(10.dp))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(id = R.string.attribute)}: ",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    RfidAttribute.entries.forEach {
                        FilterChip(
                            selected = it==settings.attribute,
                            label = {
                                Text(
                                    text = it.name,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            },
                            onClick = {
                                onAction(SettingsScreenAction.UpdateAttribute(it))
                            }
                        )
                        Spacer(modifier = modifier.width(10.dp))
                    }
                }
                Spacer(modifier = modifier.weight(1f))
                OutlinedButton(
                    modifier = modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                        onAction(SettingsScreenAction.Logout)
                    }
                ) {
                    Text(
                        modifier = modifier,
                        text = stringResource(id = R.string.exit_account),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = modifier.height(10.dp))
                OutlinedButton(
                    modifier = modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                        activity?.finish()
                    }
                ) {
                    Text(
                        modifier = modifier,
                        text = stringResource(id = R.string.exit_app),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview() {
    MobileAppAndroidForTerminalTheme {
        SettingsScreenScreen(
            state = SettingsScreenState(
                rfidSetting = RfidSetting(
                    protocol = RfidProtocol.ISO180006C,
                    attribute = RfidAttribute.TID,
                    power = 25f,
                    frequencyMode = FrequencyMode.RUSSIA
                )
            ),
            onAction = {},
            bottomRoutes = {}
        )
    }
}