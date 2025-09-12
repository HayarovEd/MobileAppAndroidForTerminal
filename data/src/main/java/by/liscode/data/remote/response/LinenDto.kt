package by.liscode.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinenDto(
    @SerialName("id")
    val id: Int,
    @SerialName("rfid")
    val rfid: String,
    @SerialName("type_id")
    val typeId: Int,
    @SerialName("type")
    val typeLinen: TypeLinenDto,
    @SerialName("discount")
    val discount: String,
    @SerialName("weight")
    val weight: Int,
    @SerialName("state")
    val state: String,
    @SerialName("wash_count")
    val washCount: Int,
    @SerialName("status")
    val status: String,
    @SerialName("price")
    val price: String,
    @SerialName("counterparty_id")
    val counterpartyId: Int,
    @SerialName("counterparty")
    val counterparty: CounterpartyDto
)
