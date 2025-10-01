package by.liscode.data.mapper

import by.liscode.data.remote.request.CounterpartyRequest
import by.liscode.data.remote.request.LinenDtoRequest
import by.liscode.data.remote.request.TypeLinenRequest
import by.liscode.data.remote.response.CounterpartyDto
import by.liscode.data.remote.response.LinenDto
import by.liscode.data.remote.response.ClientDto
import by.liscode.data.remote.response.EmployeeDto
import by.liscode.data.remote.response.TypeLinenDto
import by.liscode.domain.model.Client
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Employee
import by.liscode.domain.model.Linen
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.utils.convertToStateLinen
import by.liscode.domain.utils.convertToStateLinenString
import by.liscode.domain.utils.convertToStatusLinen
import by.liscode.domain.utils.convertToStatusLinenString

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

fun TypeLinen.convertToTypeLinenRequest(): TypeLinenRequest {
    return TypeLinenRequest(
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

fun Counterparty.convertToCounterpartyRequest(): CounterpartyRequest {
    return CounterpartyRequest(
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


fun Linen.convertToLinenDtoRequest(): LinenDtoRequest {
    return LinenDtoRequest(
        id = id,
        rfid = rfid,
        typeId = typeId,
        typeLinen = typeLinen.convertToTypeLinenRequest(),
        discount = discount,
        weight = weight,
        state = convertToStateLinenString(state),
        washCount = washCount,
        status = convertToStatusLinenString(status),
        price = price,
        counterpartyId = counterpartyId,
        counterparty = counterparty.convertToCounterpartyRequest()
    )
}

fun ClientDto.convertToClient(): Client {
    return Client(
        id = id,
        email = email,
        name = name,
        location = location,
        phone = phone,
        clientType = clientType
    )
}

fun EmployeeDto.convertToEmployee(): Employee {
    return Employee(
        id = id,
        name = name,
        position = position,
        phone = phone,
        login = login
    )
}