package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReasonWriteOffResponse(
    @SerialName("write_off_reasons")
    val writeOffReasonsDto: WriteOffReasonsDto
)