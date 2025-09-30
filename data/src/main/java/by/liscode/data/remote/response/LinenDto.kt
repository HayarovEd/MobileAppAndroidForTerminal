package by.liscode.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinenDto(
    @SerialName("client_id")
    val clientId: Int,
    @SerialName("consignment")
    val consignment: String,
    @SerialName("counterparty")
    val counterparty: CounterpartyDto,
    @SerialName("counterparty_id")
    val counterpartyId: Int,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("discount")
    val discount: String,
    @SerialName("id")
    val id: Int,
    @SerialName("nomenclature")
    val nomenclature: String,
    @SerialName("price")
    val price: String,
    @SerialName("rfid")
    val rfid: String,
    @SerialName("state")
    val state: String,
    @SerialName("status")
    val status: String,
    @SerialName("type")
    val typeLinen: TypeLinenDto,
    @SerialName("type_id")
    val typeId: Int,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("wash_count")
    val washCount: Int,
    @SerialName("weight")
    val weight: Int
)
