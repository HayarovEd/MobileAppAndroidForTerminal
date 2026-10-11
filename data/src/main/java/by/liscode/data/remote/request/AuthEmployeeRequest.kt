package by.liscode.data.remote.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthEmployeeRequest(
    @SerialName("employee_id")
    val employeeId: Int,
    @SerialName("pin")
    val pin: String
)