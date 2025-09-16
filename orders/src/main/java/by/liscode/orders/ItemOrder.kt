package by.liscode.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.Order
import by.liscode.domain.model.StatusOrder
import by.liscode.domain.utils.formateUiDateTime
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun ItemOrder(
    modifier: Modifier = Modifier,
    order: Order
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
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
                    text = "N ${order.id}",
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
            Column(
                modifier = modifier.weight(2f),
            ) {
                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = modifier,
                        text = "${order.address}",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        modifier = modifier
                            .clip(shape = RoundedCornerShape(3.dp))
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                            )
                            .padding(horizontal = 20.dp),
                        text = "N ${order.id}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
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
}