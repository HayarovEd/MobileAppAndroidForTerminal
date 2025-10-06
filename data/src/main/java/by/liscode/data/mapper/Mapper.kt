package by.liscode.data.mapper

import by.liscode.data.remote.response.ClientDto
import by.liscode.data.remote.response.CounterpartyDto
import by.liscode.data.remote.response.InventoryDto
import by.liscode.data.remote.response.LinenDto
import by.liscode.data.remote.response.OrderDto
import by.liscode.data.remote.response.TypeLinenDto
import by.liscode.domain.model.Client
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Inventory
import by.liscode.domain.model.Linen
import by.liscode.domain.model.Order
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.utils.convertStringDateToLocalDateTime
import by.liscode.domain.utils.convertToStateLinen
import by.liscode.domain.utils.convertToStatusLinen
import by.liscode.domain.utils.convertToStatusOrder

fun TypeLinenDto.convertToTypeLinen(): TypeLinen {
    return TypeLinen(
        id = this.id,
        name = this.name,
        size = this.size,
        fabric = this.fabric,
        composition = this.composition,
        color = this.color,
        density = this.density,
        sku = this.sku,
        maxWash = this.maxWash,
    )
}



fun CounterpartyDto.convertToCounterparty(): Counterparty {
    return Counterparty(
        id = id,
        name = name,
        address = address,
        inn = inn,
        phone = phone
    )
}


fun LinenDto.convertToLinen(): Linen {
    return Linen(
        id = id,
        clientId = clientId,
        rfid = rfid,
        typeId = typeId,
        typeLinen = typeLinen.convertToTypeLinen(),
        discount = discount,
        weight = weight,
        state = convertToStateLinen(state),
        washCount = washCount,
        status = convertToStatusLinen(status),
        price = price,
        counterpartyId = counterpartyId,
        counterparty = counterparty.convertToCounterparty()
    )
}


fun ClientDto.convertToClient(): Client {
    return Client(
        id = id,
        name = name,
        location = location,
        phone = phone,
        about = about,
        avatar = avatar,
        clientType = clientType,
        contactPersonPhone = contactPersonPhone,
        contactPersonFullname = contactPersonFullname,
        email = email,
        entityType = entityType,
        inn = inn,
        kpp = kpp,
        legalAddress = legalAddress,
        ogrnip = ogrnip,
        site = site
    )
}

fun OrderDto.convertToOrder(): Order {
    return Order(
        actFilePath = actFilePath,
        address = address,
        comment = comment,
        createdAt = convertStringDateToLocalDateTime(createdAt),
        creatorId = creatorId,
        hotel = hotelDto.convertToClient(),
        hotelId = hotelId,
        id = id,
        items = items.map { it.convertToLinen() },
        laundry = laundryDto.convertToClient(),
        laundryId = laundryId,
        status = convertToStatusOrder(status),
        statusComment = statusComment,
        updatedAt = convertStringDateToLocalDateTime(updatedAt)
    )
}

fun InventoryDto.convertToinventory(): Inventory {
    return Inventory(
        inStock = inStock,
        submitted = submitted,
        type = type.convertToTypeLinen()
    )
}


