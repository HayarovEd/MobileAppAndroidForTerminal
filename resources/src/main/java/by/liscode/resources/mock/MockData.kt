package by.liscode.resources.mock

import by.liscode.domain.model.Client
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Employee
import by.liscode.domain.model.Linen
import by.liscode.domain.model.Order
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.StatusOrder
import by.liscode.domain.model.TypeLinen
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

val markers = (1..21).map { "Marker N$it" }
val markers5_21 = (5..21).map { "Marker N$it" }
val markers15_45 = (15..45).map { "Marker N$it" }
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
val linens = (1..10).map {
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
val order = Order(
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
val order1 = Order(
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
val order2 = Order(
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