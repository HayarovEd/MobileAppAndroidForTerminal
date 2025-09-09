package com.abelfma.take_balance

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.FundAppBar
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.uikit.UiIconButton
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun TakeBalanceScreenRoot(
    viewModel: TakeBalanceScreenViewModel = koinViewModel(),
    onNavigateToLoginScreen: () -> Unit,
    onBackNavigate: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var visibleAddDialog by remember { mutableStateOf(false) }
    var isOpenTypeLinenMenu by remember { mutableStateOf(false) }
    var isOpenedStateLinenMenu by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiTakeBalanceEvents.LoginNavigationEvent -> onNavigateToLoginScreen()
            }
        }
    }
    TakeBalanceScreenScreen(
        state = state,
        visibleAddDialog = visibleAddDialog,
        isOpenTypeLinenMenu = isOpenTypeLinenMenu,
        isOpenedStateLinenMenu = isOpenedStateLinenMenu,
        onAction = viewModel::onAction,
        onBackNavigate = onBackNavigate,
        updateVisibilityAddDialog = {
            visibleAddDialog = it
        },
        updateVisibleTypeLinenMenu = {
            isOpenTypeLinenMenu = it
        },
        updateVisibleStateLinenMenu = {
            isOpenedStateLinenMenu = it
        }
    )
}

@Composable
fun TakeBalanceScreenScreen(
    modifier: Modifier = Modifier,
    state: TakeBalanceScreenState,
    visibleAddDialog: Boolean,
    isOpenTypeLinenMenu: Boolean,
    isOpenedStateLinenMenu: Boolean,
    updateVisibilityAddDialog: (Boolean) -> Unit,
    onAction: (TakeBalanceScreenAction) -> Unit,
    onBackNavigate: () -> Unit,
    updateVisibleTypeLinenMenu: (Boolean) -> Unit,
    updateVisibleStateLinenMenu: (Boolean) -> Unit,
) {

    if (visibleAddDialog) {
        Dialog(
            onDismissRequest = {
                updateVisibilityAddDialog(false)
            }
        ) {
            AddCounterpartyDialogContent(
                cancelUpdate = {
                    updateVisibilityAddDialog(false)
                },
                confirmUpdate = { name, address, inn, phone ->
                    onAction(
                        TakeBalanceScreenAction.AddNewCounterParty(
                            name = name,
                            address = address,
                            inn = inn,
                            phone = phone
                        )
                    )
                    updateVisibilityAddDialog(false)
                }
            )
        }
    }
    UiBaseScaffold(
        message = state.message,
        topBarContent = {
            when (state.stateScreen) {
                StateScreen.ADD -> {
                    val enabledSend = state.markers.isNotEmpty()
                            && state.currentCounterparty != null
                            && state.articleNumber.isNotBlank()
                            && state.weight.isNotBlank()
                            && state.billLading.isNotBlank()
                            && state.currentTypeLinen != null
                    FundAppBar(
                        title = stringResource(R.string.add_linen),
                        endContent = {
                            Text(
                                modifier = modifier.clickable(
                                    enabled = enabledSend,
                                    onClick = {
                                        onAction(TakeBalanceScreenAction.Send)
                                    }
                                ),
                                text = stringResource(R.string.send),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        },
                        onBack = onBackNavigate
                    )
                }

                StateScreen.COUNTERPARTY -> {
                    FundAppBar(
                        title = stringResource(R.string.counterparties),
                        endContent = {
                            UiIconButton(
                                icon = Icons.Default.Add,
                                color = MaterialTheme.colorScheme.onPrimary,
                                onClick = {
                                    updateVisibilityAddDialog(true)
                                }
                            )
                        },
                        onBack = {
                            onAction(TakeBalanceScreenAction.ChangeStateScreen(StateScreen.ADD))
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (state.stateScreen) {
            StateScreen.ADD -> {
                TakeBalanceAddScreen(
                    paddingValues  = paddingValues,
                    state = state,
                    isOpenTypeLinenMenu = isOpenTypeLinenMenu,
                    isOpenedStateLinenMenu = isOpenedStateLinenMenu,
                    updateVisibleInfo = {
                        onAction(TakeBalanceScreenAction.OpenInfo)
                    },
                    updateCurrentTypeLinen = {
                        onAction(TakeBalanceScreenAction.SetTypeLinen(it))
                    },
                    updateCurrentStateLinen = {
                        onAction(TakeBalanceScreenAction.SetStateLinen(it))
                    },
                    updateVisibleTypeLinenMenu = updateVisibleTypeLinenMenu,
                    updateVisibleStateLinenMenu = updateVisibleStateLinenMenu,
                    updateArticleNumber = {
                        onAction(TakeBalanceScreenAction.SetArticleNumber(it))
                    },
                    updateWeight = {
                        onAction(TakeBalanceScreenAction.SetWeight(it))
                    },
                    updateBillLading = {
                        onAction(TakeBalanceScreenAction.SetBillLading(it))
                    },
                    updateStateScreen = {
                        onAction(TakeBalanceScreenAction.ChangeStateScreen(it))
                    },
                    deleteMarker = {
                        onAction(TakeBalanceScreenAction.RemoveMarker(it))
                    }
                )
            }

            StateScreen.COUNTERPARTY -> {
                TakeBalanceCounterpartyScreen(
                    paddingValues  = paddingValues,
                    queryCounterparty = state.queryCounterparty,
                    isLoading = state.isLoading,
                    counterparties = state.serchedCounterParties,
                    onSearchCounterparty = {
                        onAction(TakeBalanceScreenAction.SearchCounterParty(it))
                    },
                    updateCurrentCounterParty = {
                        onAction(TakeBalanceScreenAction.SetCounterParty(it))
                        onAction(TakeBalanceScreenAction.ChangeStateScreen(StateScreen.ADD))
                    }
                )
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun Preview1() {
    MobileAppAndroidForTerminalTheme {
        TakeBalanceScreenScreen(
            state = TakeBalanceScreenState(
                stateScreen = StateScreen.COUNTERPARTY
            ),
            visibleAddDialog = false,
            isOpenedStateLinenMenu = false,
            isOpenTypeLinenMenu = true,
            updateVisibleTypeLinenMenu = {},
            updateVisibleStateLinenMenu = {},
            onAction = {},
            onBackNavigate = {},
            updateVisibilityAddDialog = {},

            )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun Preview2() {
    MobileAppAndroidForTerminalTheme {
        TakeBalanceScreenScreen(
            state = TakeBalanceScreenState(
                stateScreen = StateScreen.ADD
            ),
            visibleAddDialog = false,
            isOpenedStateLinenMenu = false,
            isOpenTypeLinenMenu = true,
            updateVisibleTypeLinenMenu = {},
            updateVisibleStateLinenMenu = {},
            onAction = {},
            onBackNavigate = {},
            updateVisibilityAddDialog = {},
        )
    }
}