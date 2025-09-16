package by.liscode.resources.uikit

import by.liscode.domain.model.ReasonWriteOff
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.StatusOrder
import by.liscode.resources.R
import by.liscode.resources.uikit.UiText.StringResource

fun ReasonWriteOff.asReasonText(): UiText {
    return when (this) {
        ReasonWriteOff.DAMAGED -> StringResource(
            R.string.damaged
        )
        ReasonWriteOff.WORN_OUT -> StringResource(
            R.string.worn_out
        )
    }
}

fun StatusLinen.asStatusLinenText(): UiText {
    return when (this) {
        StatusLinen.IN_STOCK -> StringResource(
            R.string.in_stock
        )
        StatusLinen.IN_USE -> StringResource(
            R.string.in_use
        )
        StatusLinen.IN_ORDER -> StringResource(
            R.string.in_order
        )
        StatusLinen.IN_TRANSIT -> StringResource(
            R.string.in_transit
        )
        StatusLinen.IN_PRODUCTION -> StringResource(
            R.string.in_production
        )
        StatusLinen.WRITE_OFF -> StringResource(
            R.string.write_off
        )
    }
}

fun StateLinen.asStateLinenText(): UiText {
    return when (this) {
        StateLinen.NEW -> StringResource(
            R.string.new_state
        )
        StateLinen.EXCELLENT -> StringResource(
            R.string.excellent
        )
        StateLinen.GOOD -> StringResource(
            R.string.good
        )
        StateLinen.SATISFACTORY -> StringResource(
            R.string.satisfaction
        )
        StateLinen.WRITTEN_OFF -> StringResource(
            R.string.write_off_state
        )
    }
}

fun StatusOrder.asStatusOrderText(): UiText {
    return when (this) {
        StatusOrder.REGISTERED -> StringResource(
            R.string.regestered
        )
        StatusOrder.IN_TRANSIT -> StringResource(
            R.string.order_in_transit
        )
        StatusOrder.IN_PRODUCTION -> StringResource(
            R.string.in_production
        )
        StatusOrder.FORMED -> StringResource(
            R.string.order_formed
        )
       /* StatusOrder.IN_DELIVERY -> StringResource(
            R.string.order_completed
        )*/
        StatusOrder.COMPLETED -> StringResource(
            R.string.order_completed
        )
    }
}