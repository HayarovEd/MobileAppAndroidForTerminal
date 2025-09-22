package ru.modulmoney.order_create

import by.liscode.domain.model.Employee
import by.liscode.resources.model.ScannerWork

sealed interface OrderCreateScreenAction {
    class UpdateActivityScanner(val scannerWork: ScannerWork) : OrderCreateScreenAction
    class TakeMarker(val marker: String) : OrderCreateScreenAction
    class UpdateLaundry(val laundry: Employee) : OrderCreateScreenAction
    data object CreateOrder:OrderCreateScreenAction
    data object CreateOrderWithNotApprovedMarkers:OrderCreateScreenAction
    data object CloseWarning:OrderCreateScreenAction
}