package by.liscode.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LinenInToStockRequest(
    @SerialName("consignment")
    val consignment: String,
    @SerialName("counterparty_id")
    val counterpartyId: Int,
    @SerialName("nomenclature")
    val nomenclature: String,
    @SerialName("rfids")
    val rfids: List<String>,
    @SerialName("type_id")
    val typeId: Int,
    @SerialName("weight")
    val weight: Int
)