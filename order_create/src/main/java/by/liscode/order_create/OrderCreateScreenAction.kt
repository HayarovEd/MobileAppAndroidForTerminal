package by.liscode.order_create

import by.liscode.domain.model.Client

sealed interface OrderCreateScreenAction {
    class UpdateActivityScanner(
        val isActive: Boolean) : OrderCreateScreenAction
    class TakeMarker(
        val marker: String,
        val sound: Int
    ) : OrderCreateScreenAction
    class UpdateLaundry(val laundry: Client) : OrderCreateScreenAction
    data object CreateOrder:OrderCreateScreenAction
    data object CreateOrderWithNotApprovedMarkers:OrderCreateScreenAction
    data object CloseWarning:OrderCreateScreenAction
    data object CloseSuccessDialog:OrderCreateScreenAction
    data object UpdateGrouping:OrderCreateScreenAction
}
