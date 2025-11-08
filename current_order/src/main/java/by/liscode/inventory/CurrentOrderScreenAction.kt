package by.liscode.inventory


sealed interface CurrentOrderScreenAction {
    class TakeMarker(
        val marker: String,
        val sound: Int
    ) : CurrentOrderScreenAction
    data object UpdateOrder:CurrentOrderScreenAction
    class UpdateActivityScanner(val isActive: Boolean): CurrentOrderScreenAction
    data object CloseSuccessDialog:CurrentOrderScreenAction
    data object UpdateGrouping:CurrentOrderScreenAction
    class UpdateShowExitDialog(val boolean: Boolean):CurrentOrderScreenAction
}