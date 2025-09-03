package by.liscode.domain.model

import kotlinx.datetime.LocalDateTime

data class Order(
    val id: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val hotelId: String,
    val linenId: String,
    val creatorId: String,
    val address: String,
    val comment: String,
    val status: StatusOrder,
    val commentStatus: String,
    val actFilePath: String
)