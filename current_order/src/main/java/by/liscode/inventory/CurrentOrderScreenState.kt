package by.liscode.inventory

import by.liscode.domain.model.Filter
import by.liscode.domain.model.Order
import by.liscode.domain.model.RfidSetting
import by.liscode.domain.model.UserData
import by.liscode.domain.utils.outFormat
import by.liscode.resources.uikit.UiText

data class CurrentOrderScreenState(
    val order: Order? = null,
    val userData: UserData? = null,
    val settings: RfidSetting? = null,
    val message: UiText? = null,
    val filterMarkers: List<Filter> = emptyList(),
    val isLoading: Boolean = false,
    val markers: List<String> = emptyList(),
    val isActiveScanner: Boolean = false,
) {
    val unknownMarkers = (order?.let {
        markers.filter { it !in order.items.map { linen -> linen.rfid } }
    } ?: markers)

    val sortedLinens = order?.let {
        it.items.groupBy { it.typeLinen }
            .mapValues { (_, linens) ->
                linens.sortedBy { it.typeLinen.outFormat() }
            }
    } ?: emptyMap()

}


sealed class UiCurrentOrderEvents {
    data object LoginNavigationEvent : UiCurrentOrderEvents()
}