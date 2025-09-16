package by.liscode.domain.model

data class UserData(
    val userId: Int,
    val login: String,
    val userRole: UserRole,
    val userType: UserType? = null,
    val expired: Long
)
