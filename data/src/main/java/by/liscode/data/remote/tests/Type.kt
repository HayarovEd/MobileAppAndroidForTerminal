package by.liscode.data.remote.tests


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Type(
    @SerialName("color")
    val color: String,
    @SerialName("composition")
    val composition: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("density")
    val density: String,
    @SerialName("fabric")
    val fabric: String,
    @SerialName("id")
    val id: Int,
    @SerialName("max_wash")
    val maxWash: Int,
    @SerialName("name")
    val name: String,
    @SerialName("size")
    val size: String,
    @SerialName("sku")
    val sku: String,
    @SerialName("updated_at")
    val updatedAt: String
)