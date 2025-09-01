package by.liscode.data.remote.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BasicAuthRequest(
    @SerialName("login")
    val login: String,
    @SerialName("password")
    val password: String
)