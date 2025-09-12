package ru.modulmoney.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Linen
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.FundAppBar
import by.liscode.resources.uikit.OnScan
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.uikit.UiIconButton
import by.liscode.resources.uikit.rememberRfid
import by.liscode.resources.utils.applySettings
import by.liscode.resources.utils.processScan
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun InventoryScreenRoot(
    viewModel: InventoryScreenViewModel = koinViewModel(),
    onNavigateToLoginScreen: () -> Unit,
    onBackNavigate: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction
    var isOpenTypeReasonMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scanner = rememberRfid().apply { init(context) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiInventoryEvents.LoginNavigationEvent -> onNavigateToLoginScreen()
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
            processScan(
                rfid = scanner,
                isActive = state.isActiveScanner,
                block = {
                    onAction(InventoryScreenAction.TakeMarker(marker = it))
                },
                updateIsActive = {
                    onAction(InventoryScreenAction.UpdateActivityScanner(it))
                }
            )
        }
    )
    InventoryScreenScreen(
        state = state,
        onAction = onAction,
        onBackNavigate = onBackNavigate
    )
}

@Composable
fun InventoryScreenScreen(
    modifier: Modifier = Modifier,
    state: InventoryScreenState,
    onAction: (InventoryScreenAction) -> Unit,
    onBackNavigate: () -> Unit
) {
    UiBaseScaffold(
        message = state.message,
        topBarContent = {
            FundAppBar(
                title = stringResource(R.string.inventory),
                endContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = modifier.clickable(
                                enabled = state.markers.isNotEmpty(),
                                onClick = {
                                    onAction(InventoryScreenAction.UpdateGroupping)
                                }
                            ),
                            text = stringResource(R.string.kind_upper_case),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        UiIconButton(
                            icon = Icons.Default.MoreVert,
                            color = MaterialTheme.colorScheme.onPrimary,
                            onClick = {
                                onAction(InventoryScreenAction.UpdateInventoryState(InventoryState.FILTERING))
                            }
                        )
                    }
                },
                onBack = onBackNavigate
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(15.dp),
            ) {
                CircularProgressIndicator(
                    modifier = modifier
                        .align(alignment = Alignment.Center)
                        .size(200.dp)
                )
            }
        } else {
            when (state.groupLinen) {
                GroupLinen.NAME -> {
                    TODO()
                }
                GroupLinen.TYPE_LINEN -> {
                    TODO()
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
        clientId = 1
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
        clientId = 1
    )
    val markers = (15..45).map { "Marker N$it" }
    val linens = (1..50).map {
        Linen(
            id = it,
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
    MobileAppAndroidForTerminalTheme {
        InventoryScreenScreen(
            state = InventoryScreenState(
                markers = markers,
                remoteLinens = linens
            ),
            onAction = {},
            onBackNavigate = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview1() {
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
        clientId = 1
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
        clientId = 1
    )
    val markers = (15..45).map { "Marker N$it" }
    val linens = (1..50).map {
        Linen(
            id = it,
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
    MobileAppAndroidForTerminalTheme {
        InventoryScreenScreen(
            state = InventoryScreenState(
                groupLinen = GroupLinen.TYPE_LINEN,
                markers = markers,
                remoteLinens = linens
            ),
            onAction = {},
            onBackNavigate = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview2() {
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
        clientId = 1
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
        clientId = 1
    )
    val markers = (15..45).map { "Marker N$it" }
    val linens = (1..50).map {
        Linen(
            id = it,
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
    MobileAppAndroidForTerminalTheme {
        InventoryScreenScreen(
            state = InventoryScreenState(
                markers = markers,
                remoteLinens = linens,
                filterByType = FilterByType.FilterByTypeLinen(typeLinen1)
            ),
            onAction = {},
            onBackNavigate = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
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
        clientId = 1
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
        clientId = 1
    )
    val markers = (15..45).map { "Marker N$it" }
    val linens = (1..50).map {
        Linen(
            id = it,
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
    MobileAppAndroidForTerminalTheme {
        InventoryScreenScreen(
            state = InventoryScreenState(
                markers = markers,
                remoteLinens = linens,
                filterByType = FilterByType.FilterByTypeLinen(typeLinen1),
                groupLinen = GroupLinen.TYPE_LINEN
            ),
            onAction = {},
            onBackNavigate = {}
        )
    }
}
