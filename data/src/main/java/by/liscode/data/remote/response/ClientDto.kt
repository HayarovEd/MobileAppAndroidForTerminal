package by.liscode.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientDto(
    @SerialName("about")
    val about: String,
    @SerialName("avatar")
    val avatar: String,
    @SerialName("client_type")
    val clientType: String,
    @SerialName("contact_person_fullname")
    val contactPersonFullname: String?,
    @SerialName("contact_person_phone")
    val contactPersonPhone: String?,
    @SerialName("email")
    val email: String,
    @SerialName("entity_type")
    val entityType: String,
    @SerialName("id")
    val id: Int,
    @SerialName("inn")
    val inn: String,
    @SerialName("kpp")
    val kpp: String,
    @SerialName("legal_address")
    val legalAddress: String,
    @SerialName("location")
    val location: String,
    @SerialName("name")
    val name: String,
    @SerialName("ogrnip")
    val ogrnip: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("site")
    val site: String
)
