package ru.modulmoney.order_create

import by.liscode.domain.model.Employee
import by.liscode.domain.model.Filter
import by.liscode.domain.model.Linen
import by.liscode.domain.model.RfidSetting
import by.liscode.domain.model.StatusLinen
import by.liscode.resources.model.ScannerWork
import by.liscode.resources.uikit.UiText

data class OrderCreateScreenState(
    val settings: RfidSetting? = null,
    val message: UiText? = null,
    val filterMarkers: List<Filter> = emptyList(),
    val isLoading: Boolean = false,
    val markers: List<String> = emptyList(),
    val scannerWork: ScannerWork = ScannerWork.CLEAR,
    val laundries: List<Employee>  = emptyList(),
    val remoteLinens: List<Linen> = emptyList(),
    val isShowWarning: Boolean = false,
    val currentLaundry: Employee? = null,
) {
    val approvedLinens = remoteLinens.filter { it.status == StatusLinen.IN_STOCK || it.status == StatusLinen.IN_USE}
    val notApprovedLinens = remoteLinens - approvedLinens
    val unknownMarkers = markers - remoteLinens.map { it.rfid }
    val gropedApprovedLinensByTipe = approvedLinens.groupBy { it.typeLinen }
}


sealed class UiOrderCreateEvents {
    data object LoginNavigationEvent : UiOrderCreateEvents()
}