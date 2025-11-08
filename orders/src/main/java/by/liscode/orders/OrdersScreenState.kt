package by.liscode.orders

import by.liscode.domain.model.Order
import by.liscode.domain.model.StatusOrder
import by.liscode.domain.model.UserData
import by.liscode.domain.model.UserType

data class OrdersScreenState(
    val userData: UserData? = null,
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val query: String = "",
    val sortedOrders: SortedOrders = SortedOrders.DEFAULT
) {
    private val filteredOrders = userData?.let { user ->
        when (user.userType) {
            UserType.HOTEL -> orders.filter {
                it.laundry.name.contains(query, ignoreCase = true)
                        || it.laundry.location.contains(
                    query, ignoreCase = true
                )
                        || it.id.toString().contains(query)
            }

            UserType.LAUNDRY -> orders.filter {
                it.hotel.name.contains(query, ignoreCase = true)
                        || it.hotel.location.contains(
                    query, ignoreCase = true
                )
                        || it.id.toString().contains(query)
            }

            UserType.UNKNOWN_TYPE, null -> orders
        }
    } ?: orders
    val resultOrders = when (sortedOrders) {
        SortedOrders.DEFAULT -> filteredOrders
        SortedOrders.DATE_TIME -> filteredOrders.sortedBy { it.createdAt }
        SortedOrders.STATUS -> filteredOrders.sortedBy { it.status.number }
    }.filter { it.status != StatusOrder.COMPLETED }
}


sealed class UiOrdersEvents {
    data object LoginNavigationEvent : UiOrdersEvents()
}

enum class SortedOrders {
    DEFAULT,
    DATE_TIME,
    STATUS
}