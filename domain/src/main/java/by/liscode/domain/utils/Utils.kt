package by.liscode.domain.utils

import by.liscode.domain.model.Linen
import by.liscode.domain.model.ReasonWriteOff
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.StatusOrder
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.model.UserRole
import by.liscode.domain.model.UserType
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number


fun fliterByListString(
    target: List<String>,
    etalon: List<String>,
): List<String> {
    return target.filter { itemA ->
        etalon.any { prefix -> itemA.startsWith(prefix, ignoreCase = true) }
    }
}


fun convertToUserRole(role: String): UserRole {
    return when (role) {
        "manager" -> UserRole.MANAGER
        "client" -> UserRole.CLIENT
        "client_employee" -> UserRole.CLIENT_EMPLOYEE
        else -> UserRole.UNKNOWN_ROLE
    }
}

fun convertToUserType(type: String): UserType {
    return when (type) {
        "hotel" -> UserType.HOTEL
        "laundry" -> UserType.LAUNDRY
        else -> UserType.UNKNOWN_TYPE
    }
}

fun convertToReasonWriteOff(reason: String): ReasonWriteOff {
    return when (reason) {
        "Повреждено" -> ReasonWriteOff.DAMAGED
        "Изношено" -> ReasonWriteOff.WORN_OUT
        else -> ReasonWriteOff.WORN_OUT
    }
}

fun convertToStatusLinen(status: String): StatusLinen {
    return when (status) {
        "На складе" -> StatusLinen.IN_STOCK
        "В работе" -> StatusLinen.IN_USE
        "В заказе" -> StatusLinen.IN_ORDER
        "В пути" -> StatusLinen.IN_TRANSIT
        "В производстве" -> StatusLinen.IN_PRODUCTION
        "Списан" -> StatusLinen.WRITE_OFF
        else -> StatusLinen.IN_STOCK
    }
}

fun convertToStateLinen(state: String): StateLinen {
    return when (state) {
        "Новое" -> StateLinen.NEW
        "Отличное" -> StateLinen.EXCELLENT
        "Хорошее" -> StateLinen.GOOD
        "Удовлетворительное" -> StateLinen.SATISFACTORY
        "Списано" -> StateLinen.WRITTEN_OFF
        else -> StateLinen.NEW
    }
}

fun convertToStatusOrder(status: String): StatusOrder {
    return when (status) {
        "Зарегистрирован" -> StatusOrder.REGISTERED
        "В доставке" -> StatusOrder.IN_TRANSIT
        "В производстве" -> StatusOrder.IN_PRODUCTION
        "Сформирован" -> StatusOrder.FORMED
        // "В доставке" -> StatusOrder.IN_DELIVERY
        "Выполнен" -> StatusOrder.COMPLETED
        else -> StatusOrder.REGISTERED
    }
}

fun TypeLinen.outFormat(): String {
    return "$name, $size, $fabric, $sku, $color"
}

fun Linen.printFormat(): String {
    return "${typeLinen.name}, ${typeLinen.size}, ${typeLinen.fabric}, ${typeLinen.sku}, ${typeLinen.color}, $weight"
}

fun formateUiDateTime(dateTime: LocalDateTime): String {
    return "${dateTime.time.hour}.${dateTime.time.minute}\n${dateTime.date.day}.${dateTime.date.month.number}.${dateTime.date.year}"
}


