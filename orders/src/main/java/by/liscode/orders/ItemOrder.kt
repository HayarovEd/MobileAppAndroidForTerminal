package by.liscode.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.Client
import by.liscode.domain.model.Order
import by.liscode.domain.model.StatusOrder
import by.liscode.domain.model.UserData
import by.liscode.domain.model.UserRole
import by.liscode.domain.model.UserType
import by.liscode.domain.utils.formateUiDateTime
import by.liscode.resources.mock.linens
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.asStatusOrderText
import by.liscode.resources.utils.formatWeight
import java.util.Date
import kotlin.time.ExperimentalTime

@Composable
fun ItemOrder(
    modifier: Modifier = Modifier,
    userData: UserData?,
    order: Order,
    onClickOrder: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        onClick = onClickOrder,
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = modifier
                        .clip(shape = RoundedCornerShape(3.dp))
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                        )
                        .padding(horizontal = 20.dp),
                    text = "${order.id}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = modifier.height(5.dp))
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = formateUiDateTime(order.createdAt),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = modifier.width(5.dp))
            userData?.let { user ->
                Column(
                    modifier = modifier.weight(2f),
                ) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = modifier.weight(1f),
                            text = when (user.userType) {
                                UserType.HOTEL -> order.laundry.name
                                UserType.LAUNDRY -> order.hotel.name
                                UserType.UNKNOWN_TYPE, null -> ""
                            },
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(modifier = modifier.width(5.dp))
                        Text(
                            modifier = modifier
                                //   .weight(1f)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(3.dp)
                                )
                                .padding(horizontal = 10.dp),
                            text = order.status.asStatusOrderText().asString(),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    Spacer(modifier = modifier.height(5.dp))
                    Text(
                        modifier = modifier,
                        text = when (user.userType) {
                            UserType.HOTEL -> order.laundry.location
                            UserType.LAUNDRY -> order.hotel.location
                            UserType.UNKNOWN_TYPE, null -> ""
                        },
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = TextDecoration.Underline
                    )
                    Spacer(modifier = modifier.height(5.dp))
                    Row(
                        modifier = modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = modifier.weight(1f),
                            text = when (user.userType) {
                                UserType.HOTEL -> {
                                    "${order.laundry.contactPersonFullname}, ${order.laundry.contactPersonPhone}"
                                }

                                UserType.LAUNDRY -> "${order.hotel.contactPersonFullname}, ${order.hotel.contactPersonPhone}"
                                UserType.UNKNOWN_TYPE, null -> ""
                            },
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            modifier = modifier
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(3.dp)
                                )
                                .padding(2.dp),
                            text = formatWeight(order.items.sumOf { it.weight }),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
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
private fun ItemOrderView() {
    MobileAppAndroidForTerminalTheme {
        ItemOrder(
            order = Order(
                id = 1,
                createdAt = Date(),
                /*Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault())*/
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
                    legalAddress = "Москва",
                    ogrnip = "",
                    site = ""
                ),
                items = linens,
                creatorId = 3,
                hotel = Client(
                    id = 1,
                    name = "Hotel",
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
                    legalAddress = "Москва",
                    ogrnip = "",
                    site = ""
                ),
                laundryId = 1,
                statusComment = "",
                updatedAt = Date(),
            ),
            userData = UserData(
                userId = 1,
                login = "rm",
                userRole = UserRole.CLIENT_EMPLOYEE,
                userType = UserType.HOTEL,
                expired = 0L
            ),
            onClickOrder = {}
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun ItemOrderView2() {
    MobileAppAndroidForTerminalTheme {
        ItemOrder(
            order = Order(
                id = 1,
                createdAt = Date(),
                /*Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault())*/
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
                    legalAddress = "Москва",
                    ogrnip = "",
                    site = ""
                ),
                items = linens,
                creatorId = 3,
                hotel = Client(
                    id = 1,
                    name = "Hotel",
                    location = "St.Peterburg",
                    phone = "444444",
                    about = "",
                    avatar = "",
                    clientType = "hotel",
                    contactPersonFullname = "Ivan",
                    contactPersonPhone = "4444444",
                    email = "",
                    entityType = "individual",
                    inn = "",
                    kpp = "",
                    legalAddress = "Москва",
                    ogrnip = "",
                    site = ""
                ),
                laundryId = 1,
                statusComment = "",
                updatedAt = Date(),
            ),
            userData = UserData(
                userId = 1,
                login = "rm",
                userRole = UserRole.CLIENT_EMPLOYEE,
                userType = UserType.LAUNDRY,
                expired = 0L
            ),
            onClickOrder = {}
        )
    }
}