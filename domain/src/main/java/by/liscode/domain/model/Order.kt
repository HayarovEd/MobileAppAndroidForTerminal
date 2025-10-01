package by.liscode.domain.model

import kotlinx.datetime.LocalDateTime

data class Order(
    val actFilePath: String?,
    val address: String,
    val comment: String?,
    val createdAt: LocalDateTime,
    val creatorId: Int,
    val hotel: Client,
    val hotelId: Int,
    val id: Int,
    val items: List<Linen>,
    val laundry: Client,
    val laundryId: Int,
    val status: StatusOrder,
    val statusComment: String?,
    val updatedAt: LocalDateTime
)