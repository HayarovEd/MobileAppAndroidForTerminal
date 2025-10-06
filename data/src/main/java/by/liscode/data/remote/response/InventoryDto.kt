package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InventoryDto(
    @SerialName("in_stock")
    val inStock: Int,
    @SerialName("submitted")
    val submitted: Int,
    @SerialName("type")
    val type: TypeLinenDto
)