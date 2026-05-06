package by.liscode.inventory

import by.liscode.domain.model.GroupLinen
import by.liscode.domain.model.Order
import by.liscode.domain.model.UserData
import by.liscode.domain.utils.outFormat

data class CurrentOrderScreenState(
    val currentOrder: Order? = null,
    val userData: UserData? = null,
    val isLoading: Boolean = false,
    val markers: List<String> = emptyList(),
    val isActiveScanner: Boolean = false,
    val visibilitySuccessDialog: VisibilitySuccessDialog = VisibilitySuccessDialog.INVISIBLE,
    val groupLinen: GroupLinen = GroupLinen.NAME,
    val isShowExitDialog: Boolean = false,
) {
    val unknownMarkers = (currentOrder?.let {
        markers.filter { it !in currentOrder.items.map { linen -> linen.rfid } }
    } ?: markers)

    val sortedLinens = currentOrder?.let {
        it.items.groupBy { it.typeLinen }
            .mapValues { (_, linens) ->
                linens.sortedBy { it.typeLinen.outFormat() }
            }
    } ?: emptyMap()
    val enableUpdate = currentOrder?.let {
        it.items.map {item ->  item.rfid }.all { rf-> rf in markers }
    }?:false
}


sealed class UiCurrentOrderEvents {
    data object LoginNavigationEvent : UiCurrentOrderEvents()
}

enum class VisibilitySuccessDialog{
    INVISIBLE,
    SUCCESS_INTO_USE,
    SUCCESS_AFTER_USE,
    SUCCESS_INTO_STOCK,
}