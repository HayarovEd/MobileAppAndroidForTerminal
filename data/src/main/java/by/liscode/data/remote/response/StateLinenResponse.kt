package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StateLinenResponse(
    @SerialName("item_states")
    val itemStateLinenDto: ItemStateLinenDto
)