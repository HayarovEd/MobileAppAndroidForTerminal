package by.liscode.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeesDto(
    @SerialName("employees")
    val employees: List<EmployeeDto>
)