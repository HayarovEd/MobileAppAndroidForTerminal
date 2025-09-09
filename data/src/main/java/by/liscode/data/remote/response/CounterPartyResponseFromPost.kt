package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CounterPartyResponseFromPost(
    @SerialName("counterparty")
    val counterparty: CounterpartyDto
)