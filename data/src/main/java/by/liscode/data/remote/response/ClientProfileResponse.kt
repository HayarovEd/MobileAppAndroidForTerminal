package by.liscode.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientProfileResponse(
    @SerialName("profile")
    val profile: ProfileDto
)
