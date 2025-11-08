package by.liscode.order_create

import by.liscode.domain.model.Client
import by.liscode.domain.model.GroupLinen
import by.liscode.domain.model.Linen
import by.liscode.domain.model.Order
import by.liscode.domain.model.StatusLinen

data class OrderCreateScreenState(
    val isLoading: Boolean = false,
    val order: Order? = null,
    val markers: List<String> = emptyList(),
    val isActiveScanner: Boolean = false,
    val laundries: List<Client>  = emptyList(),
    val remoteLinens: List<Linen> = emptyList(),
    val isShowWarning: Boolean = false,
    val currentLaundry: Client? = null,
    val isShowSuccessDialog: Boolean = false,
    val groupLinen: GroupLinen = GroupLinen.NAME,
) {
    val approvedLinens = remoteLinens.filter { it.status == StatusLinen.IN_STOCK || it.status == StatusLinen.IN_USE}
    val notApprovedLinens = remoteLinens - approvedLinens.toSet()
    val unknownMarkers = markers - remoteLinens.map { it.rfid }.toSet()

    val gropedApprovedLinensByType = approvedLinens.groupBy { it.typeLinen }
}


sealed class UiOrderCreateEvents {
    data object LoginNavigationEvent : UiOrderCreateEvents()
}