package by.liscode.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CounterpartyRequest(
    @SerialName("address")
    val address: String,
    @SerialName("id")
    val id: Int,
    @SerialName("inn")
    val inn: String,
    @SerialName("name")
    val name: String,
    @SerialName("phone")
    val phone: String,
)
