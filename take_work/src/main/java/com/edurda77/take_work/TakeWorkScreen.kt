package com.edurda77.take_work

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Linen
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.resources.R
import by.liscode.resources.model.ScannerWork
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.FundAppBar
import by.liscode.resources.uikit.ItemLinen
import by.liscode.resources.uikit.OnScan
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.uikit.UiIconButton
import by.liscode.resources.uikit.rememberRfid
import by.liscode.resources.utils.applySettings
import by.liscode.resources.utils.processWorkScan
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TakeWorkScreenRoot(
    viewModel: TakeWorkScreenViewModel = koinViewModel(),
    onNavigateToLoginScreen: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction
    val context = LocalContext.current
    val scanner =  rememberRfid().apply { init(context) }
    //var isActive by remember { mutableStateOf(false) }
    var scannerWork by remember { mutableStateOf(ScannerWork.CLEAR)}

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiTakeWorkEvents.LoginNavigationEvent -> onNavigateToLoginScreen()
            }
        }
    }

    LaunchedEffect(state.settings) {
        state.settings?.let {
            scanner.applySettings(it)
        }
    }
    OnScan(
        block = {
            processWorkScan(
                rfid = scanner,
                scannerWork = scannerWork,
                updateScannerWork = {
                    scannerWork = it
                },
                blockScan = {
                    onAction(TakeWorkScreenAction.TakeMarker(it))
                },
                blockClear = {
                    onAction(TakeWorkScreenAction.ClearMarkers)
                }
            )
        }
    )
    TakeWorkScreenScreen(
        state = state,
        onAction = onAction,
        onBack = onBack
    )
}

@Composable
private fun TakeWorkScreenScreen(
    modifier: Modifier = Modifier,
    state: TakeWorkScreenState,
    onAction: (TakeWorkScreenAction) -> Unit,
    onBack: () -> Unit,
) {

    UiBaseScaffold(
        message = state.message,
        topBarContent = {
            FundAppBar(
                title = stringResource(R.string.take_work),
                onBack = onBack,
                endContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = modifier,
                            text = stringResource(R.string.kind_upper_case),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        UiIconButton(
                            modifier = modifier,
                            icon = Icons.Default.MoreVert,
                            color = MaterialTheme.colorScheme.onPrimary,
                            onClick = {

                            }
                        )
                    }
                }
            )
        },
        bottomBarContent = {
            OutlinedButton(
                modifier = modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 15.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(5.dp),
                enabled = state.markers.isNotEmpty(),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                onClick = {
                   onAction(TakeWorkScreenAction.UpdateStatusToWork)
                }
            ) {
                Text(
                    modifier = modifier,
                    text = stringResource(id = R.string.take_work_upper_case),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
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
                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = modifier,
                        text = "${stringResource(R.string.remain_download)}: ${state.markers.size-state.remoteLinens.size}",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        modifier = modifier,
                        text = "${stringResource(R.string.scanned)}: ${state.markers.size}",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = modifier.height(10.dp))
                Row(
                    modifier = modifier
                        .clip(shape = RoundedCornerShape(10.dp))
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = modifier,
                        text = stringResource(R.string.our_fund),
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        modifier = modifier,
                        text = "(${state.remoteLinens.size})",
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Spacer(modifier = modifier.height(10.dp))
                LazyColumn(
                    modifier = modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    items(
                        items = state.remoteLinens,
                        key = {
                            it.id
                        }) { linen ->
                        ItemLinen(
                            linen = linen
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
    val liners = (1..10).map {
        Linen(
            id = it,
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
                clientId = 1
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
                remoteLinens = liners
            ),
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
            onAction = {},
            onBack = {}
        )
    }
}