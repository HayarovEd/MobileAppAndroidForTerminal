package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinenDtoMultiResponse(
    @SerialName("items")
    val items: List<LinenDto>
)