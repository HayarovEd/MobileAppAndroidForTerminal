package by.liscode.login


import by.liscode.resources.uikit.UiText

data class LoginScreenState(
    val login: String = "",
    val password: String = "",
    val message: UiText? = null,
    val counter: Int = 0,
    val isLoading: Boolean = false,
    val baseUrl: String = ""
)