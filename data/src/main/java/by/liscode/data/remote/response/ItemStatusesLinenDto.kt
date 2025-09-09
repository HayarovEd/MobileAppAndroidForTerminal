package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemStatusesLinenDto(
    @SerialName("in_order")
    val inOrder: String,
    @SerialName("in_production")
    val inProduction: String,
    @SerialName("in_stock")
    val inStock: String,
    @SerialName("in_transit")
    val inTransit: String,
    @SerialName("in_use")
    val inUse: String,
    @SerialName("written_off")
    val writtenOff: String
)