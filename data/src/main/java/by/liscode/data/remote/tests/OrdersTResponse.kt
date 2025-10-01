package by.liscode.data.remote.tests


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrdersTResponse(
    @SerialName("orders")
    val orders: List<Order>
)