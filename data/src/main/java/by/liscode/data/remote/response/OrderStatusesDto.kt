package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusesDto(
    @SerialName("completed")
    val completed: String,
    @SerialName("formed")
    val formed: String,
    @SerialName("in_delivery")
    val inDelivery: String,
    @SerialName("in_production")
    val inProduction: String,
    @SerialName("in_transit")
    val inTransit: String,
    @SerialName("registered")
    val registered: String
)