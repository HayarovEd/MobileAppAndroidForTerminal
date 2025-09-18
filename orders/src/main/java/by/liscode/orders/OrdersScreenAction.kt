package by.liscode.orders

sealed interface OrdersScreenAction {
    class UpdateQuery(val query: String):OrdersScreenAction
    class UpdateSorting(val sortedOrders: SortedOrders):OrdersScreenAction
}