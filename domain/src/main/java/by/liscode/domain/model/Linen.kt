package by.liscode.domain.model

data class Linen(
    val id: Int,
    val rfid: String,
    val typeId: Int,
    val typeLinen: TypeLinen,
    val discount: String,
    val weight: Int,
    val state: String,
    val washCount: Int,
    val status: String,
    val price: String,
    val counterpartyId: Int,
    val counterparty: Counterparty
)
