package by.liscode.domain.model

data class Client (
    val id: Int,
    val name: String,
    val location: String,
    val phone: String,
    val about: String,
    val avatar: String,
    val clientType: String,
    val contactPersonFullname: String?,
    val contactPersonPhone: String?,
    val email: String,
    val entityType: String,
    val inn: String,
    val kpp: String,
    val legalAddress: String,
    val ogrnip: String,
    val site: String
)