package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusLinenResponse(
    @SerialName("item_statuses")
    val itemStatusesLinenDto: ItemStatusesLinenDto
)