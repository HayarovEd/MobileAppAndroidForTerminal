package com.edurda77.take_work

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.GroupLinen
import by.liscode.domain.model.Linen
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.DividerSpacer
import by.liscode.resources.uikit.FundAppBar
import by.liscode.resources.uikit.ItemLinen
import by.liscode.resources.uikit.OnScan
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.uikit.UiButton
import by.liscode.resources.uikit.UiRowTwoText
import by.liscode.resources.uikit.UiText
import by.liscode.resources.uikit.rememberRfid
import by.liscode.resources.utils.applySettings
import by.liscode.resources.utils.processScan
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TakeWorkScreenRoot(
    viewModel: TakeWorkScreenViewModel = koinViewModel(),
    onNavigateToLoginScreen: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val baseState by viewModel.baseState.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction
    val context = LocalContext.current
    val scanner =  rememberRfid().apply { init(context) }


    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiTakeWorkEvents.LoginNavigationEvent -> onNavigateToLoginScreen()
            }
        }
    }


    LaunchedEffect(baseState.settings) {
        baseState.settings?.let {
            scanner.applySettings(it)
        }
    }
    OnScan(
        block = {
            processScan(
                rfid = scanner,
                isActive = state.isActiveScanner,
                updateIsActive = {
                    onAction(TakeWorkScreenAction.UpdateActivityScanner(it))
                },
                block = {
                    onAction(TakeWorkScreenAction.TakeMarker(marker = it, sound = R.raw.beep))
                }
            )
        },
        back = {
            scanner.free()
            onBack ()
        }
    )
    TakeWorkScreenScreen(
        state = state,
        message = baseState.message,
        onAction = onAction,
        onBack = {
            scanner.free()
            onBack ()
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TakeWorkScreenScreen(
    modifier: Modifier = Modifier,
    state: TakeWorkScreenState,
    message: UiText?,
    onAction: (TakeWorkScreenAction) -> Unit,
    onBack: () -> Unit,
) {

    if (state.isShowResultDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    modifier = modifier.clickable(
                        onClick = {
                            onAction(TakeWorkScreenAction.UpdateGrouping)
                        }
                    ),
                    text = stringResource(R.string.objects_success_in_use),
                    color = colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                Button(onClick = {
                    onAction(TakeWorkScreenAction.CloseResultDialog)                }) {
                    Text(
                        text = stringResource(R.string.ok),
                        color = colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        )
    }


    if (state.isShowWarning) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = stringResource(R.string.have_markers_not_in_stock),
                    color = colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                Button(onClick = {
                    onAction(TakeWorkScreenAction.SendAndCloseWarning)
                }) {
                    Text(
                        text = stringResource(R.string.ok),
                        color = colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        onAction(TakeWorkScreenAction.CloseWarning)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        )
    }

    UiBaseScaffold(
        message = message,
        topBarContent = {
            FundAppBar(
                title = stringResource(R.string.take_work),
                onBack = onBack,
                endContent = {
                    Text(
                        modifier = modifier.clickable(
                            onClick = {
                                onAction(TakeWorkScreenAction.UpdateGrouping)
                            }
                        ),
                        text = stringResource(R.string.kind_upper_case),
                        color = colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            )
        },
        bottomBarContent = {
            Row(
                modifier = modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .background(colorScheme.background)
                    .padding(horizontal = 15.dp),
            ) {
                UiButton(
                    title = stringResource(R.string.take_work_upper_case),
                    enabled = state.remoteLinens.isNotEmpty(),
                    onClick = {
                        onAction(TakeWorkScreenAction.UpdateStatusToWork)
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(15.dp),
            verticalArrangement = if (state.isLoading) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = modifier
                        .size(100.dp)
                )
            } else {
                UiRowTwoText(
                    title = "${stringResource(R.string.remain_download)}: ${state.markers.size-state.remoteLinens.size}",
                    value = "${stringResource(R.string.scanned)}: ${state.markers.size}",
                )
                DividerSpacer()
                when (state.groupLinen) {
                    GroupLinen.NAME -> {
                        LazyColumn(
                            modifier = modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            items(
                                items = state.remoteLinens,
                                ) { linen ->
                                ItemLinen(
                                    linen = linen
                                )
                            }
                        }
                    }
                    GroupLinen.TYPE_LINEN -> {
                        TypeLinenScreen(
                            sortedGroups = state.sortedGroups
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val linens = (1..10).map {
        Linen(
            id = it,
            clientId = 0,
            rfid = "300ed300ed300ed300ed",
            typeId = 1,
            typeLinen = TypeLinen(
                id = 1,
                name = "Пододеяльник $it",
                size = "200x220",
                fabric = "сатин",
                composition = "3x3",
                color = "белый",
                density = "100%",
                sku = "хлопок",
                maxWash = 10,
            ),
            discount = "dsk",
            weight = 135,
            state = StateLinen.NEW,
            washCount = 3,
            status = StatusLinen.IN_STOCK,
            price = "100 р.",
            counterpartyId = 3,
            counterparty = Counterparty(
                id = 3,
                name = "ООО Вектор",
                address = "Москва",
                inn = "333-333-333-333",
                phone = "+79123456789"
            )
        )
    }
    MobileAppAndroidForTerminalTheme {
        TakeWorkScreenScreen(
            state = TakeWorkScreenState(
                markers = (1..25).map { "$it"},
                remoteLinens = linens
            ),
            message = null,
            onAction = {},
            onBack = {}
        )
    }
}

@Preview
@Composable
private fun Preview2() {
    MobileAppAndroidForTerminalTheme {
        TakeWorkScreenScreen(
            state = TakeWorkScreenState(
                markers = listOf(
                    "1", "2", "3"
                ),
                isLoading = true
            ),
            message = null,
            onAction = {},
            onBack = {}
        )
    }
}

@Preview
@Composable
private fun Preview3() {
    val typeLinen1 = TypeLinen(
        id = 1,
        name = "Пододеяльник",
        size = "200x220",
        fabric = "сатин страйп 3х3",
        composition = "",
        color = "белый",
        density = "",
        sku = "100% хлопок",
        maxWash = 10,
    )
    val typeLinen2 = TypeLinen(
        id = 2,
        name = "Полотенце",
        size = "50x80",
        fabric = "сатин страйп 3х3",
        composition = "",
        color = "белый",
        density = "",
        sku = "100% хлопок",
        maxWash = 10,
    )
    val linens = (1..50).map {
        Linen(
            id = it,
            clientId = 0,
            rfid = "Marker N$it",
            typeId = it + 3,
            typeLinen = if (it % 2 == 0) typeLinen1 else typeLinen2,
            discount = "",
            weight = 135,
            state = StateLinen.SATISFACTORY,
            washCount = 9,
            status = StatusLinen.IN_STOCK,
            price = "",
            counterpartyId = 1,
            counterparty = Counterparty(
                id = it,
                name = "name $it",
                address = "Russia, Moscow, Baumana, $it",
                inn = "333-333-333-0$it",
                phone = "+7(555)55-55-222"
            )
        )
    }
    val markers = (1..70).map { "Marker N$it" }
    MobileAppAndroidForTerminalTheme {
        TakeWorkScreenScreen(
            state = TakeWorkScreenState(
                markers = markers,
                isLoading = false,
                remoteLinens = linens,
                groupLinen = GroupLinen.TYPE_LINEN
            ),
            message = null,
            onAction = {},
            onBack = {}
        )
    }
}

@Preview
@Composable
private fun Preview4() {
    MobileAppAndroidForTerminalTheme {
        TakeWorkScreenScreen(
            state = TakeWorkScreenState(
                isShowResultDialog = true
            ),
            message = null,
            onAction = {},
            onBack = {}
        )
    }
}