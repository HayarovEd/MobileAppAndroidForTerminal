package by.liscode.order_create

import by.liscode.domain.model.Client
import by.liscode.resources.model.ScannerWork

sealed interface OrderCreateScreenAction {
    class UpdateActivityScanner(val scannerWork: ScannerWork) : OrderCreateScreenAction
    class TakeMarker(val marker: String) : OrderCreateScreenAction
    class UpdateLaundry(val laundry: Client) : OrderCreateScreenAction
    data object CreateOrder:OrderCreateScreenAction
    data object CreateOrderWithNotApprovedMarkers:OrderCreateScreenAction
    data object CloseWarning:OrderCreateScreenAction
    data object CloseSuccessDialog:OrderCreateScreenAction
}
