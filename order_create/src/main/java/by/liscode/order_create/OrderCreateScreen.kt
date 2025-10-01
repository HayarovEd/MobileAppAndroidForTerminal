package by.liscode.order_create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.domain.model.Client
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Linen
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.DividerSpacer
import by.liscode.resources.uikit.NotApproveDialogContent
import by.liscode.resources.uikit.OnScan
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.uikit.UiButton
import by.liscode.resources.uikit.UiClickableRow
import by.liscode.resources.uikit.UiRowTitleTwoElements
import by.liscode.resources.uikit.UiRowTwoElements
import by.liscode.resources.uikit.UiRowTwoText
import by.liscode.resources.uikit.rememberRfid
import by.liscode.resources.utils.applySettings
import by.liscode.resources.utils.processWorkScan
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun OrderCreateScreenRoot(
    viewModel: OrderCreateScreenViewModel = koinViewModel (),
    onNavigateToLoginScreen: () -> Unit,
    onBackNavigate: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction
    val context = LocalContext.current
    val scanner = rememberRfid().apply { init(context) }
    var visibleLaundryMenu by remember { mutableStateOf(false) }
    var visibleNotApproveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiOrderCreateEvents.LoginNavigationEvent -> onNavigateToLoginScreen()
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
                scannerWork = state.scannerWork,
                updateScannerWork = {
                    onAction(OrderCreateScreenAction.UpdateActivityScanner(it))
                },
                blockScan = {
                    onAction(OrderCreateScreenAction.TakeMarker(it))
                },
                blockClear = {}
            )
        }
    )
    OrderCreateScreenScreen(
        state = state,
        onAction = viewModel::onAction,
        visibleLaundryMenu = visibleLaundryMenu,
        visibleNotApproveDialog = visibleNotApproveDialog,
        updateVisibilityLaundryMenu = {
            visibleLaundryMenu = it
        },
        updateVisibilityNotApproveDialog = {
            visibleNotApproveDialog = it
        },
        onBack = onBackNavigate
    )
}

@Composable
fun OrderCreateScreenScreen(
    modifier: Modifier = Modifier,
    state: OrderCreateScreenState,
    visibleLaundryMenu: Boolean,
    visibleNotApproveDialog: Boolean,
    updateVisibilityLaundryMenu: (Boolean) -> Unit,
    updateVisibilityNotApproveDialog: (Boolean) -> Unit,
    onAction: (OrderCreateScreenAction) -> Unit,
    onBack: () -> Unit,
) {

    if (state.isShowSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                onAction(OrderCreateScreenAction.CloseSuccessDialog)
            },
            title = {
                Text(
                    text = stringResource(R.string.linen_success_stock),
                    color = colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                Button(onClick = {
                    onAction(OrderCreateScreenAction.CloseSuccessDialog)
                }) {
                    Text(
                        text = stringResource(R.string.ok),
                        color = colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

            },
        )
    }
    if (state.isShowWarning) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = stringResource(R.string.add_not_approved),
                    color = colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                Button(onClick = {
                    onAction(OrderCreateScreenAction.CreateOrderWithNotApprovedMarkers)
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
                        onAction(OrderCreateScreenAction.CloseWarning)
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

    if (visibleNotApproveDialog) {
        Dialog(
            onDismissRequest = {
                updateVisibilityNotApproveDialog(false)
            }
        ) {
            NotApproveDialogContent(
                notApprovedLinens = state.notApprovedLinens,
                unknownMarkers = state.unknownMarkers,
                onClose = {
                    updateVisibilityNotApproveDialog(false)
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
                    .fillMaxWidth()
                    .background(colorScheme.primary)
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = modifier,
                    text = stringResource(R.string.new_order),
                    color = colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = modifier.weight(1f))
                Text(
                    modifier = modifier.clickable(
                        onClick = onBack
                    ),
                    text = stringResource(R.string.back),
                    color = colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
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
                    enabled = state.remoteLinens.isNotEmpty() && state.currentLaundry != null,
                    onClick = {
                        onAction(OrderCreateScreenAction.CreateOrder)
                    },
                    title = stringResource(R.string.create_order),
                )
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(colorScheme.background)
                    .padding(15.dp),
            ) {
                CircularProgressIndicator(
                    modifier = modifier
                        .align(alignment = Alignment.Center)
                        .size(200.dp)
                )
            }
        } else {
            Column(
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(colorScheme.background)
                    .padding(15.dp),
            ) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = modifier,
                        text = "${stringResource(R.string.laundry)}:",
                        color = colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Box {
                        Text(
                            modifier = modifier.clickable(
                                onClick = {
                                    updateVisibilityLaundryMenu(true)
                                }
                            ),
                            text = if (state.currentLaundry == null) stringResource(R.string.choose) else state.currentLaundry.name,
                            color = colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            ),
                        )
                        DropdownMenu(
                            expanded = visibleLaundryMenu,
                            onDismissRequest = {
                                updateVisibilityLaundryMenu(false)
                            }
                        ) {
                            state.laundries.forEach {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            modifier = modifier,
                                            text = it.name,
                                            color = colorScheme.onBackground,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    },
                                    onClick = {
                                        onAction(OrderCreateScreenAction.UpdateLaundry(it))
                                        updateVisibilityLaundryMenu(false)
                                    }
                                )
                            }
                        }
                    }
                }
                state.currentLaundry?.let { laundry ->
                    Spacer(modifier = modifier.height(10.dp))
                    UiRowTwoText(
                        title = "${stringResource(R.string.address)}:",
                        value = laundry.location,
                    )
                    Spacer(modifier = modifier.height(10.dp))
                    UiRowTwoText(
                        title = "${stringResource(R.string.contact)}:",
                        value = laundry.contactPersonFullname?:"",
                    )
                    Spacer(modifier = modifier.height(10.dp))
                    UiRowTwoText(
                        title = "${stringResource(R.string.phone)}:",
                        value = laundry.contactPersonPhone?:"",
                    )
                }
                DividerSpacer()
                UiRowTwoText(
                    title = stringResource(R.string.type_linen),
                    value = stringResource(R.string.reading),
                )
                DividerSpacer()
                if (state.markers.isEmpty() && state.remoteLinens.isEmpty()) {
                    Text(
                        modifier = modifier.fillMaxWidth(),
                        text = stringResource(R.string.empty_objects),
                        color = colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                } else {
                    UiClickableRow(
                        title = stringResource(R.string.not_for_order),
                        value = (state.notApprovedLinens.size + state.unknownMarkers.size).toString(),
                        onClick = {
                            updateVisibilityNotApproveDialog(true)
                        }
                    )
                    DividerSpacer()
                    UiRowTitleTwoElements(
                        title = stringResource(R.string.fund),
                        firstNumber = state.approvedLinens.size
                    )
                    Spacer(modifier = modifier.height(8.dp))
                    LazyColumn(
                        modifier = modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        items(state.gropedApprovedLinensByTipe.keys.toList()) { typeLinen ->
                            UiRowTwoElements(
                                title = typeLinen,
                                firstNumber = state.gropedApprovedLinensByTipe[typeLinen]?.size?:0,
                                fontColor = colorScheme.primary
                            )
                        }
                    }
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
        OrderCreateScreenScreen(
            state = OrderCreateScreenState(
                isShowWarning = true
            ),
            visibleLaundryMenu = false,
            visibleNotApproveDialog = false,
            onAction = {},
            onBack = {},
            updateVisibilityLaundryMenu = {},
            updateVisibilityNotApproveDialog = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview1() {
    MobileAppAndroidForTerminalTheme {
        OrderCreateScreenScreen(
            state = OrderCreateScreenState(),
            visibleLaundryMenu = false,
            visibleNotApproveDialog = false,
            onAction = {},
            onBack = {},
            updateVisibilityLaundryMenu = {},
            updateVisibilityNotApproveDialog = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview2() {
    val laundries = (1..5).map {
        Client(
            id = it,
            name = "ООО Ромашка N$it",
            location = "Москва",
            phone = "7894561237",
            about = "",
            avatar = "",
            clientType = "laundry",
            contactPersonFullname = "Dima",
            contactPersonPhone = "7894561237",
            email = "",
            entityType = "individual",
            inn = "",
            kpp = "",
            legalAddress =  "Москва",
            ogrnip = "",
            site = ""
        )
    }
    MobileAppAndroidForTerminalTheme {
        OrderCreateScreenScreen(
            state = OrderCreateScreenState(
                currentLaundry = laundries.first(),
                laundries = laundries
            ),
            visibleLaundryMenu = false,
            visibleNotApproveDialog = false,
            onAction = {},
            onBack = {},
            updateVisibilityLaundryMenu = {},
            updateVisibilityNotApproveDialog = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview3() {
    val laundries = (1..5).map {
        Client(
            id = it,
            name = "ООО Ромашка N$it",
            location = "Москва",
            phone = "7894561237",
            about = "",
            avatar = "",
            clientType = "laundry",
            contactPersonFullname = "Dima",
            contactPersonPhone = "7894561237",
            email = "",
            entityType = "individual",
            inn = "",
            kpp = "",
            legalAddress =  "Москва",
            ogrnip = "",
            site = ""
        )
    }
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
    val markers = (1..70).map { "Marker N$it" }
    val linens = (1..50).map {
        Linen(
            id = it,
            clientId = 0,
            rfid = "Marker N$it",
            typeId = it + 3,
            typeLinen = if (it % 2 == 0) typeLinen1 else typeLinen2,
            discount = "",
            weight = 135,
            state = StateLinen.entries.random(),
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
    MobileAppAndroidForTerminalTheme {
        OrderCreateScreenScreen(
            state = OrderCreateScreenState(
                currentLaundry = laundries.first(),
                laundries = laundries,
                remoteLinens = linens,
                markers = markers
            ),
            visibleLaundryMenu = false,
            visibleNotApproveDialog = false,
            onAction = {},
            onBack = {},
            updateVisibilityLaundryMenu = {},
            updateVisibilityNotApproveDialog = {}
        )
    }
}