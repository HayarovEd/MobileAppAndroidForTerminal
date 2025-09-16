package by.liscode.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeesResponse(
    @SerialName("employees")
    val employees: List<EmployeeDto>
)
