package by.liscode.domain.model

data class Client (
    val id: Int,
    val email: String,
    val name: String,
    val location: String,
    val phone: String,
    val clientType: String,
)