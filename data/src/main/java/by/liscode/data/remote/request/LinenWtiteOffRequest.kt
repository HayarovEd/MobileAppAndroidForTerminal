package by.liscode.data.remote.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinenWtiteOffRequest(
    @SerialName("comment")
    val comment: String,
    @SerialName("reason")
    val reason: String,
    @SerialName("rfids")
    val rfids: List<String>
)