package ru.modulmoney.current_order

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.domain.model.Client
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Employee
import by.liscode.domain.model.Linen
import by.liscode.domain.model.Order
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.StatusOrder
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.model.UserData
import by.liscode.domain.model.UserRole
import by.liscode.domain.model.UserType
import by.liscode.domain.utils.outFormat
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.NotApproveDialogContent
import by.liscode.resources.uikit.OnScan
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.uikit.rememberRfid
import by.liscode.resources.utils.applySettings
import by.liscode.resources.utils.processScan
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun CurrentOrderScreenRoot(
    viewModel: CurrentOrderScreenViewModel = koinViewModel(),
    onNavigateToLoginScreen: () -> Unit,
    onBackNavigate: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction
    val context = LocalContext.current
    val scanner = rememberRfid().apply { init(context) }
    var visibleNotApproveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiCurrentOrderEvents.LoginNavigationEvent -> onNavigateToLoginScreen()
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
                    onAction(CurrentOrderScreenAction.TakeMarker(marker = it))
                },
                updateIsActive = {
                    onAction(CurrentOrderScreenAction.UpdateActivityScanner(it))
                }
            )
        }
    )

    CurrentOrderScreenScreen(
        state = state,
        visibleNotApproveDialog = visibleNotApproveDialog,
        onAction = viewModel::onAction,
        updateVisibilityNotApproveDialog = {
            visibleNotApproveDialog = it
        },
        onBack = onBackNavigate
    )
}

@Composable
fun CurrentOrderScreenScreen(
    modifier: Modifier = Modifier,
    state: CurrentOrderScreenState,
    visibleNotApproveDialog: Boolean,
    onAction: (CurrentOrderScreenAction) -> Unit,
    updateVisibilityNotApproveDialog: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    if (visibleNotApproveDialog) {
        Dialog(
            onDismissRequest = {
                updateVisibilityNotApproveDialog(false)
            }
        ) {
            NotApproveDialogContent(
                notApprovedLinens = emptyList(),
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
                    text = "${stringResource(R.string.order)} ${state.order?.id}",
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
                state.order?.let { order ->
                    state.userData?.let { userData ->
                        val enabled =
                            (state.sortedLinens[false] == null || state.sortedLinens[false]?.values?.isEmpty() == true)
                                    && (state.sortedLinens[true] != null && state.sortedLinens[true]?.values?.isNotEmpty() == true)
                        if (userData.userType == UserType.HOTEL
                            && userData.userRole == UserRole.CLIENT_EMPLOYEE
                            && (order.status == StatusOrder.IN_DELIVERY || order.status == StatusOrder.FORMED)
                        ) {
                            ActionButton(
                                title = stringResource(R.string.complete_order),
                                enabled = enabled,
                                onClick = {
                                    onAction(CurrentOrderScreenAction.UpdateOrder(StatusOrder.COMPLETED))
                                }
                            )
                        }
                        if (userData.userType == UserType.LAUNDRY
                            && userData.userRole == UserRole.CLIENT_EMPLOYEE
                            && (order.status == StatusOrder.REGISTERED || order.status == StatusOrder.IN_TRANSIT)
                        ) {
                            ActionButton(
                                title = stringResource(R.string.take_work),
                                enabled = enabled,
                                onClick = {
                                    onAction(CurrentOrderScreenAction.UpdateOrder(StatusOrder.IN_PRODUCTION))
                                }
                            )
                        }
                        if (userData.userType == UserType.LAUNDRY
                            && userData.userRole == UserRole.CLIENT_EMPLOYEE
                            && (order.status == StatusOrder.IN_PRODUCTION)
                        ) {
                            ActionButton(
                                title = stringResource(R.string.complete_work),
                                enabled = enabled,
                                onClick = {
                                    onAction(CurrentOrderScreenAction.UpdateOrder(StatusOrder.FORMED))
                                }
                            )
                        }
                    }
                }
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
                state.order?.let { order ->
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
                        Text(
                            modifier = modifier,
                            text = order.employee.name,
                            color = colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Spacer(modifier = modifier.height(10.dp))
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = modifier,
                            text = "${stringResource(R.string.address)}:",
                            color = colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            modifier = modifier,
                            text = order.employee.position,
                            color = colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Spacer(modifier = modifier.height(10.dp))
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = modifier,
                            text = "${stringResource(R.string.contact)}:",
                            color = colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            modifier = modifier,
                            text = order.employee.login,
                            color = colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Spacer(modifier = modifier.height(10.dp))
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = modifier,
                            text = "${stringResource(R.string.phone)}:",
                            color = colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            modifier = modifier,
                            text = order.employee.phone,
                            color = colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Spacer(modifier = modifier.height(4.dp))
                    HorizontalDivider(
                        modifier = modifier.fillMaxWidth(),
                        thickness = 1.dp
                    )
                    Spacer(modifier = modifier.height(8.dp))
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    updateVisibilityNotApproveDialog(true)
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = modifier,
                            text = stringResource(R.string.not_for_order),
                            color = colorScheme.error,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            modifier = modifier,
                            text = state.unknownMarkers.size.toString(),
                            color = colorScheme.error,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    Spacer(modifier = modifier.height(4.dp))
                    HorizontalDivider(
                        modifier = modifier.fillMaxWidth(),
                        thickness = 1.dp
                    )
                    Spacer(modifier = modifier.height(8.dp))
                    Row(
                        modifier = modifier
                            .clip(shape = RoundedCornerShape(10.dp))
                            .fillMaxWidth()
                            .background(colorScheme.secondary)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = modifier.weight(6f),
                            text = stringResource(R.string.fund),
                            color = colorScheme.onSecondary,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = modifier.width(5.dp))
                        Text(
                            modifier = modifier.weight(1f),
                            text = order.linens.size.toString(),
                            color = colorScheme.onSecondary,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = modifier.width(5.dp))
                        Text(
                            modifier = modifier.weight(1f),
                            text = order.linens.size.toString(),
                            color = colorScheme.onSecondary,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    Spacer(modifier = modifier.height(8.dp))
                    LazyColumn(
                        modifier = modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        state.sortedLinens.keys.forEach { status ->
                            val linensByType = state.sortedLinens[status]?.keys?.toList()?: emptyList()
                            items(linensByType) { type->
                                val linens = state.sortedLinens[status]?.get(type)?.map { it.rfid }?: emptyList()
                                Row(
                                    modifier = modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    val fontColor = if (status) colorScheme.primary else colorScheme.error
                                    Text(
                                        modifier = modifier.weight(5f),
                                        text = type.outFormat(),
                                        color = fontColor,
                                        style = MaterialTheme.typography.titleSmall,
                                    )
                                    Spacer(modifier = modifier.width(5.dp))
                                    Text(
                                        modifier = modifier.weight(1f),
                                        text = linens.count { element->  markers.contains(element) }.toString(),
                                        color = fontColor,
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = modifier.width(5.dp))
                                    Text(
                                        modifier = modifier.weight(1f),
                                        text = linens.size.toString(),
                                        color = fontColor,
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Center
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

private val markers = (1..20).map { "Marker N$it" }
private val typeLinen1 = TypeLinen(
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
private val typeLinen2 = TypeLinen(
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
private val linens = (1..10).map {
    Linen(
        id = it,
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

@OptIn(ExperimentalTime::class)
private val order = Order(
    id = "1",
    createdAt = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    updatedAt = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    hotelId = "1",
    linenId = "2",
    creatorId = "User",
    address = "Moscow",
    comment = "",
    status = StatusOrder.FORMED,
    commentStatus = "",
    actFilePath = "",
    client = Client(
        id = 1,
        email = "",
        name = "Тринадцатый Климент Романович",
        location = "Москва",
        phone = "7894561236",
        clientType = ""
    ),
    employee = Employee(
        id = 1,
        name = "ООО Ромашка",
        position = "Москва",
        phone = "7894561237",
        login = "Dima"
    ),
    linens = linens
)

@OptIn(ExperimentalTime::class)
private val order1 = Order(
    id = "1",
    createdAt = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    updatedAt = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    hotelId = "1",
    linenId = "2",
    creatorId = "User",
    address = "Moscow",
    comment = "",
    status = StatusOrder.REGISTERED,
    commentStatus = "",
    actFilePath = "",
    client = Client(
        id = 1,
        email = "",
        name = "Тринадцатый Климент Романович",
        location = "Москва",
        phone = "7894561236",
        clientType = ""
    ),
    employee = Employee(
        id = 1,
        name = "ООО Ромашка",
        position = "Москва",
        phone = "7894561237",
        login = "Dima"
    ),
    linens = linens
)

@OptIn(ExperimentalTime::class)
private val order2 = Order(
    id = "1",
    createdAt = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    updatedAt = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    hotelId = "1",
    linenId = "2",
    creatorId = "User",
    address = "Moscow",
    comment = "",
    status = StatusOrder.IN_PRODUCTION,
    commentStatus = "",
    actFilePath = "",
    client = Client(
        id = 1,
        email = "",
        name = "Тринадцатый Климент Романович",
        location = "Москва",
        phone = "7894561236",
        clientType = ""
    ),
    employee = Employee(
        id = 1,
        name = "ООО Ромашка",
        position = "Москва",
        phone = "7894561237",
        login = "Dima"
    ),
    linens = linens
)

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    title: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        enabled = enabled,
        onClick = onClick
    ) {
        Text(
            modifier = modifier,
            text = title,
            color = colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview() {
    MobileAppAndroidForTerminalTheme {
        CurrentOrderScreenScreen(
            state = CurrentOrderScreenState(),
            visibleNotApproveDialog = false,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {}
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
        CurrentOrderScreenScreen(
            state = CurrentOrderScreenState(),
            visibleNotApproveDialog = true,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {}
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview2() {

    MobileAppAndroidForTerminalTheme {
        CurrentOrderScreenScreen(
            state = CurrentOrderScreenState(
                markers = markers,
                order = order
            ),
            visibleNotApproveDialog = false,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview3() {
    MobileAppAndroidForTerminalTheme {
        CurrentOrderScreenScreen(
            state = CurrentOrderScreenState(
                userData = UserData(
                    userId = 1,
                    login = "Dima",
                    userRole = UserRole.CLIENT,
                    userType = UserType.HOTEL,
                    expired = 0L
                ),
                order = order,
                markers = markers
            ),
            visibleNotApproveDialog = true,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {}
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview4() {
    MobileAppAndroidForTerminalTheme {
        CurrentOrderScreenScreen(
            state = CurrentOrderScreenState(
                userData = UserData(
                    userId = 1,
                    login = "Dima",
                    userRole = UserRole.CLIENT_EMPLOYEE,
                    userType = UserType.HOTEL,
                    expired = 0L
                ),
                order = order,
                markers = markers
            ),
            visibleNotApproveDialog = true,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {}
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview5() {
    MobileAppAndroidForTerminalTheme {
        CurrentOrderScreenScreen(
            state = CurrentOrderScreenState(
                userData = UserData(
                    userId = 1,
                    login = "Dima",
                    userRole = UserRole.CLIENT_EMPLOYEE,
                    userType = UserType.LAUNDRY,
                    expired = 0L
                ),
                order = order1,
                markers = markers
            ),
            visibleNotApproveDialog = true,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {}
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview6() {
    MobileAppAndroidForTerminalTheme {
        CurrentOrderScreenScreen(
            state = CurrentOrderScreenState(
                userData = UserData(
                    userId = 1,
                    login = "Dima",
                    userRole = UserRole.CLIENT_EMPLOYEE,
                    userType = UserType.LAUNDRY,
                    expired = 0L
                ),
                order = order2,
                markers = markers
            ),
            visibleNotApproveDialog = true,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {}
        )
    }
}

