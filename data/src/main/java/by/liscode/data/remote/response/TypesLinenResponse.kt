package by.liscode.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TypesLinenResponse(
    @SerialName("types")
    val types: List<TypeLinenDto>
)
