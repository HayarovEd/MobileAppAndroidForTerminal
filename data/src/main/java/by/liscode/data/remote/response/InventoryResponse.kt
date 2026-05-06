package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InventoryResponse(
    @SerialName("inventory")
    val inventoryDto: List<InventoryDto>
)