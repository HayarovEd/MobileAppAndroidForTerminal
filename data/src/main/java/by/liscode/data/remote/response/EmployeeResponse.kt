package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeeResponse(
    @SerialName("employee")
    val employee: EmployeeDto
)