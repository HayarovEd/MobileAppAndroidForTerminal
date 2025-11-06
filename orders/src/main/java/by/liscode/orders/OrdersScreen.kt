package by.liscode.orders

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.domain.model.Client
import by.liscode.domain.model.Order
import by.liscode.domain.model.StatusOrder
import by.liscode.domain.model.UserData
import by.liscode.domain.model.UserRole
import by.liscode.domain.model.UserType
import by.liscode.resources.R
import by.liscode.resources.mock.linens
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.uikit.UiIconButton
import by.liscode.resources.uikit.UiTextField
import by.liscode.resources.utils.BaseState
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import java.util.Date
import kotlin.time.ExperimentalTime

@Composable
fun OrdersScreenRoot(
    viewModel: OrdersScreenViewModel = koinViewModel(),
    bottomRoutes: @Composable () -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    onNavigateToCreateOrderScreen: () -> Unit,
    onNavigateToOrderScreen: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val baseState by viewModel.baseState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiOrdersEvents.LoginNavigationEvent -> onNavigateToLoginScreen()
            }
        }
    }
    OrdersScreenScreen(
        state = state,
        baseState = baseState,
        onAction = viewModel::onAction,
        onNavigateToOrderScreen = onNavigateToOrderScreen,
        onNavigateToCreateOrderScreen = onNavigateToCreateOrderScreen,
        bottomRoutes = bottomRoutes
    )
}

@Composable
fun OrdersScreenScreen(
    modifier: Modifier = Modifier,
    state: OrdersScreenState,
    baseState: BaseState,
    bottomRoutes: @Composable () -> Unit,
    onNavigateToCreateOrderScreen: () -> Unit,
    onNavigateToOrderScreen: (Int) -> Unit,
    onAction: (OrdersScreenAction) -> Unit,
) {
    UiBaseScaffold(
        message = baseState.message,
        topBarContent = {
            Row(
                modifier = modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .background(colorScheme.primary)
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = modifier,
                    text = stringResource(R.string.orders),
                    color = colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = modifier.weight(1f))
                Text(
                    modifier = modifier.clickable(
                        onClick = {
                            onAction(OrdersScreenAction.UpdateSorting(SortedOrders.DATE_TIME))
                        }
                    ),
                    text = stringResource(R.string.date_time),
                    color = colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = modifier.width(15.dp))
                Text(
                    modifier = modifier.clickable(
                        onClick = {
                            onAction(OrdersScreenAction.UpdateSorting(SortedOrders.STATUS))
                        }
                    ),
                    text = stringResource(R.string.status),
                    color = colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodySmall,
                )
                state.userData?.let { userData ->
                    if (userData.userType == UserType.HOTEL && userData.userRole == UserRole.CLIENT_EMPLOYEE)
                        UiIconButton(
                            modifier = modifier,
                            icon = Icons.Default.Add,
                            color = colorScheme.onPrimary,
                            onClick = onNavigateToCreateOrderScreen
                        )
                }
            }
        },
        bottomBarContent = bottomRoutes
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
                    modifier = modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                        tint = colorScheme.onBackground
                    )
                    UiTextField(
                        content = state.query,
                        label = stringResource(R.string.order_query_ps),
                        onClickContent = {
                            onAction(OrdersScreenAction.UpdateQuery(it))
                        }
                    )
                }
                Spacer(modifier = modifier.height(10.dp))
                LazyColumn(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    items(state.resultOrders) { order ->
                        ItemOrder(
                            order = order,
                            onClickOrder = {
                                onNavigateToOrderScreen(order.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun Preview() {
    val orders = (1..10).map {
        Order(
            id = 1,
            createdAt = Date(),
            hotelId = 1,
            address = "Moscow",
            comment = "",
            status = StatusOrder.FORMED,
            actFilePath = "",
            laundry = Client(
                id = 1,
                name = "ООО Ромашка",
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
            ),
            items = linens,
            creatorId = 3,
            hotel = Client(
                id = 1,
                name = "ООО Ромашка",
                location = "Москва",
                phone = "7894561237",
                about = "",
                avatar = "",
                clientType = "hotel",
                contactPersonFullname = "Ivan",
                contactPersonPhone = "7894561237",
                email = "",
                entityType = "individual",
                inn = "",
                kpp = "",
                legalAddress =  "Москва",
                ogrnip = "",
                site = ""
            ),
            laundryId = 1,
            statusComment = "",
            updatedAt = Date(),
        )
    }
    MobileAppAndroidForTerminalTheme {
        OrdersScreenScreen(
            state = OrdersScreenState(
                orders = orders,
                userData = UserData(
                    userId = 1,
                    login = "",
                    userRole = UserRole.CLIENT,
                    userType = UserType.HOTEL,
                    expired = 0L
                )
            ),
            baseState = BaseState(),
            onAction = {},
            bottomRoutes = {},
            onNavigateToOrderScreen = {},
            onNavigateToCreateOrderScreen = {}
        )
    }
}