package by.liscode.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.Client
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Linen
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.ItemLinen
import by.liscode.resources.uikit.UiRowTitleTwoElements

@Composable
fun TypeNameScreen(
    modifier: Modifier = Modifier,
    clients: List<Client>,
    outMarkers: List<String>,
    groupedLinen:  Map<Int, List<Linen>>,
    ) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        groupedLinen.keys.sortedBy { it }.forEach { clientId->
            val client = clients.firstOrNull{
                it.id == clientId
            }
            stickyHeader {
                UiRowTitleTwoElements(
                    title = client?.name?:stringResource(R.string.fund),
                    firstNumber = groupedLinen[clientId]?.size?:0
                )
            }
            items(groupedLinen[clientId]?:emptyList()) { linen->
                ItemLinen(
                    linen = linen
                )
            }
        }
        if (outMarkers.isNotEmpty()) {
            stickyHeader {
                UiRowTitleTwoElements(
                    title = stringResource(R.string.unknown_markers),
                    firstNumber = outMarkers.size
                )
            }
            items(outMarkers) { marker ->
                Text(
                    modifier = modifier,
                    text = "${stringResource(R.string.rfid)}: $marker",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyMedium,
                )
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
    val linens = (1..49).map {
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

    val clients = (0..4).map {
        Client(
            id = it,
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
        )
    }
    val markers = (1..70).map { "Marker N$it" }
    val outMarkers = markers - linens.map { it.rfid }
    MobileAppAndroidForTerminalTheme {
        TypeNameScreen(
            groupedLinen = linens.groupBy {it.clientId  },
            clients = clients,
            outMarkers = outMarkers
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
    val clientId = 2
    val linens = (1..49).map {
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
    val clients = (0..4).map {
        Client(
            id = it,
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
        )
    }
    val markers = (1..70).map { "Marker N$it" }
    val outMarkers = markers - linens.map { it.rfid }
    MobileAppAndroidForTerminalTheme {
        TypeNameScreen(
            groupedLinen = linens.filter { it.clientId == clientId }.groupBy {it.clientId  },
            clients = clients,
            outMarkers = outMarkers
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
    val linens = (1..49).map {
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
    val clients = (0..4).map {
        Client(
            id = it,
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
        )
    }
    MobileAppAndroidForTerminalTheme {
        TypeNameScreen(
            groupedLinen = linens.filter { it.rfid == "Marker N5" }.groupBy {it.clientId  },
            clients = clients,
            outMarkers = emptyList()
        )
    }
}
