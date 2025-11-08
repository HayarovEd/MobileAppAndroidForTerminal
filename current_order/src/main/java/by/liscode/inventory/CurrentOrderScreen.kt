package by.liscode.inventory

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.domain.model.GroupLinen
import by.liscode.domain.model.StatusOrder
import by.liscode.domain.model.UserData
import by.liscode.domain.model.UserRole
import by.liscode.domain.model.UserType
import by.liscode.resources.R
import by.liscode.resources.mock.markers
import by.liscode.resources.mock.markers5_21
import by.liscode.resources.mock.order
import by.liscode.resources.mock.order1
import by.liscode.resources.mock.order2
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.DividerSpacer
import by.liscode.resources.uikit.FundAppBar
import by.liscode.resources.uikit.ItemLinen
import by.liscode.resources.uikit.NotApproveDialogContent
import by.liscode.resources.uikit.OnScan
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.uikit.UiButton
import by.liscode.resources.uikit.UiClickableRow
import by.liscode.resources.uikit.UiRowThreeElements
import by.liscode.resources.uikit.UiRowTitleThreeElements
import by.liscode.resources.uikit.UiRowTitleTwoElements
import by.liscode.resources.uikit.UiRowTwoElements
import by.liscode.resources.uikit.UiRowTwoText
import by.liscode.resources.uikit.rememberRfid
import by.liscode.resources.utils.BaseState
import by.liscode.resources.utils.applySettings
import by.liscode.resources.utils.processScan
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import kotlin.time.ExperimentalTime

@Composable
fun CurrentOrderScreenRoot(
    viewModel: CurrentOrderScreenViewModel = koinViewModel(),
    onNavigateToLoginScreen: () -> Unit,
    onBackNavigate: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val baseState by viewModel.baseState.collectAsStateWithLifecycle()
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

    LaunchedEffect(baseState.settings) {
        baseState.settings?.let {
            scanner.applySettings(it)
        }
    }
    val isWorkWithOrder =
        state.userData?.let { userData ->
            (userData.userType == UserType.HOTEL
                    && userData.userRole == UserRole.CLIENT_EMPLOYEE
                    && (order.status == StatusOrder.IN_DELIVERY || order.status == StatusOrder.FORMED)
                    ) || (userData.userType == UserType.LAUNDRY
                    && userData.userRole == UserRole.CLIENT_EMPLOYEE
                    && (order.status == StatusOrder.REGISTERED || order.status == StatusOrder.IN_TRANSIT)
                    ) || (userData.userType == UserType.LAUNDRY
                    && userData.userRole == UserRole.CLIENT_EMPLOYEE
                    && (order.status == StatusOrder.IN_PRODUCTION)
                    )
        } ?: false
    if (isWorkWithOrder) {
        OnScan(
            block = {
                processScan(
                    rfid = scanner,
                    isActive = state.isActiveScanner,
                    block = {
                        onAction(
                            CurrentOrderScreenAction.TakeMarker(
                                marker = it,
                                sound = R.raw.beep
                            )
                        )
                    },
                    updateIsActive = {
                        onAction(CurrentOrderScreenAction.UpdateActivityScanner(it))
                    }
                )
            },
            back = {
                if (state.markers.isNotEmpty()) {
                    onAction(CurrentOrderScreenAction.UpdateShowExitDialog(true))
                } else {
                    scanner.free()
                    onBackNavigate()
                }
            }
        )
    }

    CurrentOrderScreenScreen(
        state = state,
        baseState = baseState,
        isWorkWithOrder = isWorkWithOrder,
        visibleNotApproveDialog = visibleNotApproveDialog,
        onAction = viewModel::onAction,
        updateVisibilityNotApproveDialog = {
            visibleNotApproveDialog = it
        },
        onBack = {
            if (state.markers.isNotEmpty()) {
                onAction(CurrentOrderScreenAction.UpdateShowExitDialog(true))
            } else {
                scanner.free()
                onBackNavigate()
            }
        },
        onClickExit = {
            onAction(CurrentOrderScreenAction.UpdateShowExitDialog(false))
            scanner.free()
            onBackNavigate()
        }
    )
}

@Composable
fun CurrentOrderScreenScreen(
    modifier: Modifier = Modifier,
    state: CurrentOrderScreenState,
    baseState: BaseState,
    isWorkWithOrder: Boolean,
    visibleNotApproveDialog: Boolean,
    onAction: (CurrentOrderScreenAction) -> Unit,
    updateVisibilityNotApproveDialog: (Boolean) -> Unit,
    onClickExit: () -> Unit,
    onBack: () -> Unit,
) {
    val successMessage = when (state.visibilitySuccessDialog) {
        VisibilitySuccessDialog.INVISIBLE -> ""
        VisibilitySuccessDialog.SUCCESS_INTO_USE -> stringResource(R.string.order_into_use)
        VisibilitySuccessDialog.SUCCESS_AFTER_USE -> stringResource(R.string.order_after_use)
        VisibilitySuccessDialog.SUCCESS_INTO_STOCK -> stringResource(R.string.order_comleted)
    }
    if (state.isShowExitDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = stringResource(R.string.sure_exit_create_order),
                    color = colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                Button(onClick = onClickExit) {
                    Text(
                        text = stringResource(R.string.yes),
                        color = colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        onAction(CurrentOrderScreenAction.UpdateShowExitDialog(false))
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
    if (state.visibilitySuccessDialog != VisibilitySuccessDialog.INVISIBLE) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    modifier = modifier.clickable(
                        onClick = {
                            onAction(CurrentOrderScreenAction.CloseSuccessDialog)
                        }
                    ),
                    text = successMessage,
                    color = colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                Button(onClick = {
                    onAction(CurrentOrderScreenAction.CloseSuccessDialog)
                    onBack()
                }) {
                    Text(
                        text = stringResource(R.string.ok),
                        color = colorScheme.onPrimary,
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
                notApprovedLinens = emptyList(),
                unknownMarkers = state.unknownMarkers,
                onClose = {
                    updateVisibilityNotApproveDialog(false)
                }
            )
        }
    }

    UiBaseScaffold(
        message = baseState.message,
        topBarContent = {
            FundAppBar(
                title = "${stringResource(R.string.order)} ${state.order?.id}",
                endContent = {
                    Text(
                        modifier = modifier.clickable(
                            enabled = state.markers.isNotEmpty(),
                            onClick = {
                                onAction(CurrentOrderScreenAction.UpdateGrouping)
                            }
                        ),
                        text = stringResource(R.string.kind_upper_case),
                        color = colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                onBack = onBack
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
                state.order?.let { order ->
                    state.userData?.let { userData ->
                        val enabled = state.enableUpdate
                        if (userData.userType == UserType.HOTEL
                            && userData.userRole == UserRole.CLIENT_EMPLOYEE
                            && (order.status == StatusOrder.IN_DELIVERY || order.status == StatusOrder.FORMED)
                        ) {
                            UiButton(
                                title = stringResource(R.string.complete_order),
                                enabled = enabled,
                                onClick = {
                                    onAction(CurrentOrderScreenAction.UpdateOrder)
                                }
                            )
                        }
                        if (userData.userType == UserType.LAUNDRY
                            && userData.userRole == UserRole.CLIENT_EMPLOYEE
                            && (order.status == StatusOrder.REGISTERED || order.status == StatusOrder.IN_TRANSIT)
                        ) {
                            UiButton(
                                title = stringResource(R.string.take_work),
                                enabled = enabled,
                                onClick = {
                                    onAction(CurrentOrderScreenAction.UpdateOrder)
                                }
                            )
                        }
                        if (userData.userType == UserType.LAUNDRY
                            && userData.userRole == UserRole.CLIENT_EMPLOYEE
                            && (order.status == StatusOrder.IN_PRODUCTION)
                        ) {
                            UiButton(
                                title = stringResource(R.string.complete_work),
                                enabled = enabled,
                                onClick = {
                                    onAction(CurrentOrderScreenAction.UpdateOrder)
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
                    UiRowTwoText(
                        title = "${stringResource(R.string.laundry)}:",
                        value = order.laundry.name,
                    )
                    Spacer(modifier = modifier.height(10.dp))
                    UiRowTwoText(
                        title = "${stringResource(R.string.address)}:",
                        value = order.laundry.location,
                    )
                    Spacer(modifier = modifier.height(10.dp))
                    UiRowTwoText(
                        title = "${stringResource(R.string.contact)}:",
                        value = order.laundry.contactPersonFullname ?: "",
                    )
                    Spacer(modifier = modifier.height(10.dp))
                    UiRowTwoText(
                        title = "${stringResource(R.string.phone)}:",
                        value = order.laundry.contactPersonPhone ?: "",
                    )
                    if (state.unknownMarkers.isNotEmpty()) {
                        DividerSpacer()
                        UiClickableRow(
                            title = stringResource(R.string.not_for_order),
                            value = state.unknownMarkers.size.toString(),
                            onClick = {
                                updateVisibilityNotApproveDialog(true)
                            }
                        )
                    }
                    Spacer(modifier = modifier.height(4.dp))
                    HorizontalDivider(
                        modifier = modifier.fillMaxWidth(),
                        thickness = 1.dp
                    )
                    Spacer(modifier = modifier.height(8.dp))
                    if (isWorkWithOrder) {
                        UiRowTitleThreeElements(
                            title = stringResource(R.string.fund),
                            firstNumber = order.items.size,
                            secondNumber = order.items.map { it.rfid }
                                .filter { it in state.markers }.size
                        )
                    } else {
                        UiRowTitleTwoElements(
                            title = stringResource(R.string.fund),
                            firstNumber = order.items.size,
                        )
                    }

                    Spacer(modifier = modifier.height(8.dp))
                    LazyColumn(
                        modifier = modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        when (state.groupLinen) {
                            GroupLinen.NAME -> {
                                items(state.order.items) { linen ->
                                    ItemLinen(linen = linen)
                                }
                            }

                            GroupLinen.TYPE_LINEN -> {
                                items(state.sortedLinens.keys.toList()) { type ->
                                    if (isWorkWithOrder) {
                                        val existLinen = state.sortedLinens[type] ?: emptyList()
                                        val fontColor = if (existLinen.map { it.rfid }
                                                .all { it in state.markers }) colorScheme.primary else colorScheme.error
                                        UiRowThreeElements(
                                            title = type,
                                            fontColor = fontColor,
                                            firstNumber = state.sortedLinens[type]?.size ?: 0,
                                            secondNumber = existLinen.map { it.rfid }
                                                .filter { it in state.markers }.size
                                        )
                                    } else {
                                        UiRowTwoElements(
                                            title = type,
                                            fontColor = colorScheme.primary,
                                            firstNumber = state.sortedLinens[type]?.size ?: 0,
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
            baseState = BaseState(),
            isWorkWithOrder = true,
            visibleNotApproveDialog = false,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {},
            onClickExit = {},
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
            baseState = BaseState(),
            visibleNotApproveDialog = true,
            isWorkWithOrder = true,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {},
            onClickExit = {},
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
            baseState = BaseState(),
            isWorkWithOrder = true,
            visibleNotApproveDialog = false,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {},
            onClickExit = {},
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview21() {

    MobileAppAndroidForTerminalTheme {
        CurrentOrderScreenScreen(
            state = CurrentOrderScreenState(
                order = order
            ),
            baseState = BaseState(),
            isWorkWithOrder = false,
            visibleNotApproveDialog = false,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {},
            onClickExit = {},
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
            isWorkWithOrder = true,
            baseState = BaseState(),
            visibleNotApproveDialog = true,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {},
            onClickExit = {},
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
            isWorkWithOrder = true,
            baseState = BaseState(),
            visibleNotApproveDialog = true,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {},
            onClickExit = {},
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
            isWorkWithOrder = true,
            baseState = BaseState(),
            visibleNotApproveDialog = true,
            onAction = {},
            updateVisibilityNotApproveDialog = {},
            onBack = {},
            onClickExit = {},
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
                markers = markers5_21
            ),
            visibleNotApproveDialog = true,
            onAction = {},
            isWorkWithOrder = true,
            baseState = BaseState(),
            updateVisibilityNotApproveDialog = {},
            onBack = {},
            onClickExit = {},
        )
    }
}

