package by.liscode.inventory

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.GroupLinen
import by.liscode.domain.model.Linen
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.utils.outFormat
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.FundAppBar
import by.liscode.resources.uikit.OnScan
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.uikit.UiIconButton
import by.liscode.resources.uikit.UiOutlandedButton
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
    val context = LocalContext.current
    val scanner = rememberRfid().apply { init(context) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiInventoryEvents.LoginNavigationEvent -> onNavigateToLoginScreen()
            }
        }
    }
    Log.d("TEST WORK SCANNER APP", "filterByType ${state.filterByType} ")
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
        },
        back = {
            scanner.free()
            onBackNavigate ()
        }
    )
    InventoryScreenScreen(
        state = state,
        onAction = onAction,
        onBackNavigate = {
            scanner.free()
            onBackNavigate ()
        }
    )
}

@Composable
fun InventoryScreenScreen(
    modifier: Modifier = Modifier,
    state: InventoryScreenState,
    onAction: (InventoryScreenAction) -> Unit,
    onBackNavigate: () -> Unit
) {

    if (state.isShowSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                onAction(InventoryScreenAction.CloseSuccessDialog)
            },
            title = {
                Text(
                    text = stringResource(R.string.data_inventory_sended),
                    color = colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                Button(onClick = {
                    onAction(InventoryScreenAction.CloseSuccessDialog)
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
    UiBaseScaffold(
        message = state.message,
        topBarContent = {
            when (state.inventoryState) {
                InventoryState.CHECKING -> {
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
                                            onAction(InventoryScreenAction.UpdateGrouping)
                                        }
                                    ),
                                    text = stringResource(R.string.kind_upper_case),
                                    color = colorScheme.onPrimary,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                UiIconButton(
                                    icon = Icons.Default.MoreVert,
                                    color = colorScheme.onPrimary,
                                    onClick = {
                                        onAction(
                                            InventoryScreenAction.UpdateInventoryState(
                                                InventoryState.FILTERING
                                            )
                                        )
                                    }
                                )
                            }
                        },
                        onBack = onBackNavigate
                    )
                }

                InventoryState.FILTERING -> {
                    FundAppBar(
                        title = stringResource(R.string.filter),
                        onBack = {
                            onAction(InventoryScreenAction.UpdateInventoryState(InventoryState.CHECKING))
                        }
                    )
                }
            }
        },
        bottomBarContent = {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(colorScheme.background)
            ) {
                if (state.inventoryState == InventoryState.CHECKING) {
                    UiOutlandedButton(
                        modifier = modifier
                            .navigationBarsPadding()
                            .padding(horizontal = 15.dp)
                            .fillMaxWidth(),
                        title = stringResource(R.string.save_result),
                        enabled = !state.isActiveScanner
                                && state.markers.isNotEmpty()
                                && state.remoteLinens.isNotEmpty(),
                        onClick = {
                            onAction(InventoryScreenAction.SaveResult)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (state.inventoryState) {
            InventoryState.CHECKING -> {
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
                        when (state.groupLinen) {
                            GroupLinen.NAME -> {
                                TypeNameScreen(
                                    size = state.remoteLinens.size,
                                    markers = state.markers,
                                    sortedLinen = state.finishSortedLines
                                )
                            }

                            GroupLinen.TYPE_LINEN -> {
                                TypeLinenScreen(
                                    size = state.remoteLinens.size,
                                    markers = state.markers,
                                    sortedGroups = state.finishFilterGroups
                                )
                            }
                        }
                    }
                }
            }

            InventoryState.FILTERING -> {
                Column(
                    modifier = modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(colorScheme.background)
                        .padding(15.dp),
                ) {
                    Text(
                        modifier = modifier,
                        text = stringResource(R.string.choose_flter_type),
                        color = colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Spacer(modifier = modifier.height(4.dp))
                    HorizontalDivider(
                        modifier = modifier.fillMaxWidth(),
                        thickness = 1.dp
                    )
                    Spacer(modifier = modifier.height(4.dp))
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = state.filterByType == FilterByType.ALL,
                            onClick = {
                                onAction(InventoryScreenAction.UpdateFilter(FilterByType.ALL))
                            }
                        )
                        Text(
                            modifier = modifier,
                            text = stringResource(R.string.show_all),
                            color = colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = modifier.height(4.dp))
                    HorizontalDivider(
                        modifier = modifier.fillMaxWidth(),
                        thickness = 1.dp
                    )
                    Spacer(modifier = modifier.height(4.dp))
                    if (state.linenTypes.isNotEmpty()) {
                       //var currentTypeLinen by remember { mutableStateOf(state.linenTypes[0]) }
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = state.filterByType == FilterByType.FILTER_BY_TYPE_LINEN,
                                onClick = {
                                    onAction(
                                        InventoryScreenAction.UpdateFilter(
                                            FilterByType.FILTER_BY_TYPE_LINEN
                                        )
                                    )
                                    onAction(
                                        InventoryScreenAction.UpdateCurrentType(
                                            state.linenTypes.first()
                                        )
                                    )
                                }
                            )
                            Text(
                                modifier = modifier,
                                text = stringResource(R.string.on_type),
                                color = colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = modifier.height(4.dp))
                        HorizontalDivider(
                            modifier = modifier.fillMaxWidth(),
                            thickness = 1.dp
                        )
                        Spacer(modifier = modifier.height(4.dp))
                        if (state.filterByType == FilterByType.FILTER_BY_TYPE_LINEN) {
                            LazyColumn(
                                modifier = modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                items(state.linenTypes) { linenType ->
                                    Text(
                                        modifier = modifier
                                            .border(
                                                width = 1.dp,
                                                color = if (state.currentTypeLinen == linenType) colorScheme.primary else colorScheme.outline,
                                                shape = RoundedCornerShape(15.dp)
                                            )
                                            .clip(RoundedCornerShape(15.dp))
                                            .fillMaxWidth()
                                            .padding(15.dp)
                                            .clickable(
                                                onClick = {
                                                    onAction(
                                                        InventoryScreenAction.UpdateCurrentType(
                                                            linenType
                                                        )
                                                    )
                                                }
                                            ),
                                        text = linenType.outFormat(),
                                        color = if (state.currentTypeLinen == linenType) colorScheme.primary else colorScheme.onBackground,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                        ),
                                    )
                                }
                            }
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
    val markers = (15..45).map { "Marker N$it" }
    val linens = (1..50).map {
        Linen(
            id = it,
            rfid = "Marker N$it",
            clientId = 0,
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
    val markers = (15..45).map { "Marker N$it" }
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
    val markers = (15..45).map { "Marker N$it" }
    val linens = (1..50).map {
        Linen(
            id = it,
            rfid = "Marker N$it",
            clientId = 0,
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
                filterByType = FilterByType.FILTER_BY_TYPE_LINEN,
                currentTypeLinen = typeLinen1
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
    val markers = (15..45).map { "Marker N$it" }
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
    MobileAppAndroidForTerminalTheme {
        InventoryScreenScreen(
            state = InventoryScreenState(
                markers = markers,
                remoteLinens = linens,
                filterByType = FilterByType.FILTER_BY_TYPE_LINEN,
                currentTypeLinen = typeLinen1,
                groupLinen = GroupLinen.TYPE_LINEN
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
private fun Preview4() {
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
    MobileAppAndroidForTerminalTheme {
        InventoryScreenScreen(
            state = InventoryScreenState(
                remoteLinens = linens,
                filterByType = FilterByType.FILTER_BY_TYPE_LINEN,
                currentTypeLinen = typeLinen1,
                inventoryState = InventoryState.FILTERING
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
private fun Preview5() {
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
    MobileAppAndroidForTerminalTheme {
        InventoryScreenScreen(
            state = InventoryScreenState(
                remoteLinens = linens,
                filterByType = FilterByType.ALL,
                inventoryState = InventoryState.FILTERING
            ),
            onAction = {},
            onBackNavigate = {}
        )
    }
}
