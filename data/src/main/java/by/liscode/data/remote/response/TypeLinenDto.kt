package by.liscode.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TypeLinenDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("size")
    val size: String,
    @SerialName("fabric")
    val fabric: String,
    @SerialName("composition")
    val composition: String,
    @SerialName("color")
    val color: String,
    @SerialName("density")
    val density: String,
    @SerialName("sku")
    val sku: String,
    @SerialName("max_wash")
    val maxWash: Int,
)
