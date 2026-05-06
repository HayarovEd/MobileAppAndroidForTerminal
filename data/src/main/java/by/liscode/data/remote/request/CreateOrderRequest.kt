package by.liscode.data.remote.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    @SerialName("address")
    val address: String,
    @SerialName("laundry_id")
    val laundryId: Int,
    @SerialName("rfids")
    val rfids: List<String>,
    @SerialName("send_act")
    val sendAct: Boolean
)