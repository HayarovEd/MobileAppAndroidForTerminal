package by.liscode.splash


sealed class UiSplashEvents {
    data object FundNavigateEvent : UiSplashEvents()
    data object LoginNavigateEvent : UiSplashEvents()
}