package by.liscode.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientProfileResponse(
    @SerialName("clients")
    val clients: List<ProfileDto>
)
