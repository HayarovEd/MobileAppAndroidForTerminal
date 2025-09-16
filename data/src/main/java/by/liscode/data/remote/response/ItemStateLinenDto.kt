package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemStateLinenDto(
    @SerialName("excellent")
    val excellent: String,
    @SerialName("good")
    val good: String,
    @SerialName("new")
    val new: String,
    @SerialName("satisfactory")
    val satisfactory: String,
    @SerialName("written_off")
    val writtenOff: String
)