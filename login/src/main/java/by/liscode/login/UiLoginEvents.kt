package by.liscode.login


sealed class UiLoginEvents {
    data object RegisterNavigateEvent : UiLoginEvents()
    data object FundNavigateEvent : UiLoginEvents()
}