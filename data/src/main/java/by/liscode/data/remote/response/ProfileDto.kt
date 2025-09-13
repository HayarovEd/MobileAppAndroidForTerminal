package by.liscode.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    @SerialName("id")
    val id: Int,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedSt: String,
    @SerialName("email")
    val email: String,
    @SerialName("book_email")
    val bookEmail: String,
    @SerialName("name")
    val name: String,
    @SerialName("gender")
    val gander: String,
    @SerialName("birth_date")
    val birthDate: String,
    @SerialName("site")
    val site: String,
    @SerialName("about")
    val about: String,
    @SerialName("location")
    val location: String,
    @SerialName("status")
    val status: String,
    @SerialName("last_visit_at")
    val lastVisitAt: String,
    @SerialName("avatar")
    val avatar: String,
    @SerialName("email_confirmed")
    val emailConfirmed: Boolean,
    @SerialName("phone")
    val phone: String,
    @SerialName("inn")
    val inn: String,
    @SerialName("kpp")
    val kpp: String,
    @SerialName("ogrnip")
    val ogrnip: String,
    @SerialName("legal_address")
    val legalAddress: String,
    @SerialName("discount")
    val discount: String,
    @SerialName("entity_type")
    val entityType: String,
    @SerialName("balance")
    val balance: String,
    @SerialName("client_type")
    val clientType: String,
)
