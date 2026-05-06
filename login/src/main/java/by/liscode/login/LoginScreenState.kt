package by.liscode.login


import by.liscode.domain.model.Employee

data class LoginScreenState(
    val employees: List<Employee> = emptyList(),
    val selectedEmployee: Employee? = null,
    val password: String = "",
    val counter: Int = 0,
    val isLoading: Boolean = false,
    val baseUrl: String = "",
    val typeTerminalBearer: TypeTerminalBearer = TypeTerminalBearer.OK,
)

enum class TypeTerminalBearer {
    OK,
    EMPTY,
    EXPIRED
}