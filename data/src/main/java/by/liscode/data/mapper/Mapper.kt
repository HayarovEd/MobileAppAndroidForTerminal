package by.liscode.data.mapper

import by.liscode.data.remote.response.CounterpartyDto
import by.liscode.data.remote.response.LinenDto
import by.liscode.data.remote.response.TypeLinenDto
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Linen
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.utils.convertToStateLinen
import by.liscode.domain.utils.convertToStatusLinen

fun TypeLinenDto.convertToTypeLinen() : TypeLinen {
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
        clientId = clientId
    )
}


fun CounterpartyDto.convertToCounterparty() : Counterparty {
    return Counterparty(
        id = id,
        name = name,
        address = address,
        inn = inn,
        phone = phone
    )
}

fun LinenDto.convertToLinen() : Linen {
    return Linen(
        id = id,
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