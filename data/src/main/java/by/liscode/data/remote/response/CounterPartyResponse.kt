package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CounterPartyResponse(
    @SerialName("counterparties")
    val counterparties: List<CounterpartyDto>
)