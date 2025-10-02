package by.liscode.inventory


sealed interface CurrentOrderScreenAction {
    class TakeMarker(val marker: String) : CurrentOrderScreenAction
    data object UpdateOrder:CurrentOrderScreenAction
    class UpdateActivityScanner(val isActive: Boolean): CurrentOrderScreenAction
    data object CloseSuccessDialog:CurrentOrderScreenAction
}