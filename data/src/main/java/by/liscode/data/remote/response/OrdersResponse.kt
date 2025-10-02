package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrdersResponse(
    @SerialName("orders")
    val orderDtos: List<OrderDtoShort>
)