package ru.modulmoney.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Linen
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.utils.outFormat
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme

@Composable
fun TypeNameScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    size: Int,
    markers: List<String>,
    sortedLinen:  Map<Boolean, List<Linen>>,

) {
    Column(
        modifier = modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(15.dp),
    ) {
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
                text = stringResource(R.string.our_fund),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = modifier.width(5.dp))
            Text(
                modifier = modifier.weight(1f),
                text = size.toString(),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = modifier.width(5.dp))
            Text(
                modifier = modifier.weight(1f),
                text = markers.size.toString(),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Spacer(modifier = modifier.height(10.dp))
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            sortedLinen.keys.forEach { status ->
                val linensByType = sortedLinen[status]?: emptyList()
                items(linensByType) { linen->
                    Column (
                        modifier = modifier.fillMaxWidth(),
                    ){
                        val fontColor = if (status) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        Text(
                            modifier = modifier.fillMaxWidth(),
                            text = linen.typeLinen.outFormat(),
                            color = fontColor,
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            modifier = modifier.fillMaxWidth(),
                            text = linen.rfid,
                            color = fontColor,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
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
    val filterByType = FilterByType.All
    val sortedLinens = linens.groupBy {
        it.rfid in markers
    }
        .mapValues { (_, linens) ->
            linens.sortedBy { it.typeLinen.outFormat() }
        }


    MobileAppAndroidForTerminalTheme {
        TypeNameScreen(
            paddingValues = PaddingValues(10.dp),
            markers = markers,
            size = linens.size,
            sortedLinen = sortedLinens
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
    val filterByType = FilterByType.FilterByTypeLinen(typeLinen2)
    val sortedLinens = linens.groupBy {
        it.rfid in markers
    }
        .mapValues { (_, linens) ->
            linens.sortedBy { it.typeLinen.outFormat() }
        }

    val finishSordetLines = when  (filterByType) {
        FilterByType.All -> sortedLinens
        is FilterByType.FilterByTypeLinen -> {
            sortedLinens.mapValues { (_, innerMap) ->
                innerMap.filter { it.typeLinen == filterByType.typeLinen}
            }.filterValues { it.isNotEmpty() }
        }
    }
    MobileAppAndroidForTerminalTheme {
        TypeNameScreen(
            paddingValues = PaddingValues(10.dp),
            markers = markers,
            size = linens.size,
            sortedLinen = finishSordetLines
        )
    }
}
