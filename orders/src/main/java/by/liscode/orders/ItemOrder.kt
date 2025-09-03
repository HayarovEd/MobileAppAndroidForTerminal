package by.liscode.orders

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import by.liscode.domain.model.Order
import by.liscode.domain.model.StatusOrder
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun ItemOrder(
    modifier: Modifier = Modifier,
    order: Order
) {
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun ItemOrderView() {
    ItemOrder(
        order = Order(
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
            actFilePath = ""
        )
    )
}