package by.liscode.domain.model

data class Linen(
    val id: Int,
    val rfid: String,
    val typeId: Int,
    val typeLinen: TypeLinen,
    val discount: String,
    val weight: Int,
    val state: StateLinen,
    val washCount: Int,
    val status: StatusLinen,
    val price: String,
    val counterpartyId: Int,
    val counterparty: Counterparty
)
