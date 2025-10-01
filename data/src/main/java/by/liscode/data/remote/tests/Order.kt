package by.liscode.data.remote.tests


import by.liscode.data.remote.response.ClientDto
import by.liscode.data.remote.response.LinenDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    @SerialName("act_file_path")
    val actFilePath: String?,
    @SerialName("address")
    val address: String,
    @SerialName("comment")
    val comment: String?,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("creator_id")
    val creatorId: Int,
    @SerialName("hotel")
    val hotel: ClientDto,
    @SerialName("hotel_id")
    val hotelId: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("items")
    val items: List<LinenDto>,
    @SerialName("laundry")
    val laundry: ClientDto,
    @SerialName("laundry_id")
    val laundryId: Int,
    @SerialName("status")
    val status: String,
    @SerialName("status_comment")
    val statusComment: String?,
    @SerialName("updated_at")
    val updatedAt: String
)