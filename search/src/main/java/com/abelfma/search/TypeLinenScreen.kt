package com.abelfma.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Linen
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.utils.outFormat
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme

@Composable
fun TypeLinenScreen(
    modifier: Modifier = Modifier,
    groupedLinen:  Map<Int, Map<TypeLinen, List<Linen>>>,
    ) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        groupedLinen.keys.sortedBy { it }.forEach { clientId->
            stickyHeader {
                Row(
                    modifier = modifier
                        .clip(shape = RoundedCornerShape(10.dp))
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = modifier.weight(6f),
                        text = clientId.toString(),
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = modifier.width(5.dp))
                    Text(
                        modifier = modifier.weight(1f),
                        text = groupedLinen[clientId]?.values?.sumOf { it .size}.toString(),
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            val groupByLinenType = groupedLinen[clientId]?:emptyMap()
            items(groupByLinenType.keys.toList()) { type->
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        modifier = modifier.weight(5f),
                        text = type.outFormat(),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Spacer(modifier = modifier.width(5.dp))
                    Text(
                        modifier = modifier.weight(1f),
                        text = groupByLinenType[type]?.size.toString(),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }
}

@Preview(
    showBackground = true,
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
    val linens = (1..49).map {
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
        TypeLinenScreen(
            groupedLinen = linens.groupBy { it.typeLinen.clientId }
                .mapValues { (_, linens) ->
                    linens.groupBy { it.typeLinen }
                }
        )
    }
}
@Preview(
    showBackground = true,
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
        clientId = 2
    )
    val clientId = 2
    val linens = (1..49).map {
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
        TypeLinenScreen(
            groupedLinen = linens.filter { it.typeLinen.clientId == clientId }.groupBy { it.typeLinen.clientId }
                .mapValues { (_, linens) ->
                    linens.groupBy { it.typeLinen }
                }
        )
    }
}
@Preview(
    showBackground = true,
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
        clientId = 2
    )
    val linens = (1..49).map {
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
        TypeLinenScreen(
            groupedLinen = linens.filter { it.rfid == "Marker N5" }.groupBy { it.typeLinen.clientId }
                .mapValues { (_, linens) ->
                    linens.groupBy { it.typeLinen }
                }
        )
    }
}
