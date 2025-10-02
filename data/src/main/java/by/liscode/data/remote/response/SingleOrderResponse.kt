package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SingleOrderResponse(
    @SerialName("order")
    val orderDtoShort: OrderDtoShort
)