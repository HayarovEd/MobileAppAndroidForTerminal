package by.liscode.orders

import by.liscode.domain.model.Order
import by.liscode.domain.model.UserData

data class OrdersScreenState(
    val userData: UserData? = null,
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val query: String = "",
    val sortedOrders: SortedOrders = SortedOrders.DEFAULT
) {
    private val filteredOrders = orders.filter {
                it.laundry.name.contains(query)
                || it.address.contains(
            query
        )
    }
    val resultOrders = when (sortedOrders) {
        SortedOrders.DEFAULT -> filteredOrders
        SortedOrders.DATE_TIME -> filteredOrders.sortedBy { it.createdAt }
        SortedOrders.STATUS -> filteredOrders.sortedBy { it.status.number }
    }
}


sealed class UiOrdersEvents {
    data object LoginNavigationEvent : UiOrdersEvents()
}

enum class SortedOrders {
    DEFAULT,
    DATE_TIME,
    STATUS
}