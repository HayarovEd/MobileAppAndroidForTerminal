package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthDto(
    @SerialName("token")
    val token: String
)