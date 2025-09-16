package by.liscode.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientProfileResponse(
    @SerialName("client")
    val client: ClientDto
)
