package by.liscode.regisration

import by.liscode.resources.uikit.UiText

data class RegistrationScreenState(
    val message: UiText? = null,
    val url: String = "",
    val login: String = "",
    val password: String = "",
    val terminalId: String = "",
    val isLoading: Boolean = false,
   // val isRegistered: Boolean = false
)