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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


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
        "damaged" -> ReasonWriteOff.DAMAGED
        "worn_out" -> ReasonWriteOff.WORN_OUT
        else -> ReasonWriteOff.WORN_OUT
    }
}

fun convertToStatusLinen(status: String): StatusLinen {
    return when (status) {
        "in_stock" -> StatusLinen.IN_STOCK
        "in_use" -> StatusLinen.IN_USE
        "in_order" -> StatusLinen.IN_ORDER
        "in_transit" -> StatusLinen.IN_TRANSIT
        "in_production" -> StatusLinen.IN_PRODUCTION
        "written_off" -> StatusLinen.WRITE_OFF
        else -> StatusLinen.IN_STOCK
    }
}

fun convertToStatusLinenString(statusLinen: StatusLinen): String {
    return when (statusLinen) {
        StatusLinen.IN_STOCK -> "in_stock"
        StatusLinen.IN_USE -> "in_use"
        StatusLinen.IN_ORDER -> "in_order"
        StatusLinen.IN_TRANSIT -> "in_transit"
        StatusLinen.IN_PRODUCTION -> "in_production"
        StatusLinen.WRITE_OFF -> "written_off"
    }
}

fun convertToStateLinen(state: String): StateLinen {
    return when (state) {
        "new" -> StateLinen.NEW
        "excellent" -> StateLinen.EXCELLENT
        "good" -> StateLinen.GOOD
        "satisfactory" -> StateLinen.SATISFACTORY
        "written_off" -> StateLinen.WRITTEN_OFF
        else -> StateLinen.NEW
    }
}

fun convertToStateLinenString(stateLinen: StateLinen): String {
    return when (stateLinen) {
        StateLinen.NEW -> "new"
        StateLinen.EXCELLENT -> "excellent"
        StateLinen.GOOD -> "good"
        StateLinen.SATISFACTORY -> "satisfactory"
        StateLinen.WRITTEN_OFF -> "written_off"
    }
}

fun convertToStatusOrder(status: String): StatusOrder {
    return when (status) {
        "registered" -> StatusOrder.REGISTERED
        "in_transit" -> StatusOrder.IN_TRANSIT
        "in_production" -> StatusOrder.IN_PRODUCTION
        "formed" -> StatusOrder.FORMED
         "in_delivery" -> StatusOrder.IN_DELIVERY
        "completed" -> StatusOrder.COMPLETED
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

@OptIn(ExperimentalTime::class)
fun convertStringDateToLocalDateTime (dateString: String): LocalDateTime {
    val instant = Instant.parse(dateString)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}


