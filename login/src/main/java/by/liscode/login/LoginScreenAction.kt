package by.liscode.login

import by.liscode.domain.model.Employee

sealed interface LoginScreenAction {
    data class UpdateSelectedEmployee(val employee: Employee) : LoginScreenAction
    data class UpdatePassword(val password: String) : LoginScreenAction
    data object OnLogin: LoginScreenAction
    data object UpdateCounter: LoginScreenAction
}