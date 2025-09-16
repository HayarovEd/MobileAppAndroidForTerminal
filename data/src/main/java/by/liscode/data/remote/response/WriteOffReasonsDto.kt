package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WriteOffReasonsDto(
    @SerialName("damaged")
    val damaged: String,
    @SerialName("worn_out")
    val wornOut: String
)