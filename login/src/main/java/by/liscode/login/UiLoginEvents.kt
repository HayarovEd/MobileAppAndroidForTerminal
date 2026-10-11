package by.liscode.login

import by.liscode.resources.uikit.UiText


sealed interface UiLoginEvents {
    data object RegisterNavigateEvent : UiLoginEvents
    data object FundNavigateEvent : UiLoginEvents
    data class OnMessage(val message: UiText) : UiLoginEvents
}