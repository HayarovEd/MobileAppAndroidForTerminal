package by.liscode.regisration

sealed interface RegistrationScreenAction {
    data class UpdateUrl(val url: String) : RegistrationScreenAction
    data class UpdateLogin(val login: String) : RegistrationScreenAction
    data class UpdatePassword(val password: String) : RegistrationScreenAction
    data object SaveUrl: RegistrationScreenAction
    data object RegisterTerminal: RegistrationScreenAction
}