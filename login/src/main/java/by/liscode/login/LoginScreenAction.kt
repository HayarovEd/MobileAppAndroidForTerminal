package by.liscode.login

sealed interface LoginScreenAction {
    data class UpdateLogin(val login: String) : LoginScreenAction
    data class UpdatePassword(val password: String) : LoginScreenAction
    data object Login: LoginScreenAction
    data object UpdateCounter: LoginScreenAction
}