package by.liscode.data.remote.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RfidInToUseRequest(
    @SerialName("rfids")
    val rfids: List<String>
)