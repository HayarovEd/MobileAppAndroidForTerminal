package by.liscode.data.remote.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateOrderRequest(
    @SerialName("ignore_missing")
    val ignoreMissing: Boolean,
    @SerialName("rfids")
    val rfids: List<String>
)