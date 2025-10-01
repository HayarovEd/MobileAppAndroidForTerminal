package by.liscode.data.remote.tests


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("consignment")
    val consignment: String,
    @SerialName("counterparty")
    val counterparty: Counterparty,
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
    val type: Type,
    @SerialName("type_id")
    val typeId: Int,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("wash_count")
    val washCount: Int,
    @SerialName("weight")
    val weight: Int
)