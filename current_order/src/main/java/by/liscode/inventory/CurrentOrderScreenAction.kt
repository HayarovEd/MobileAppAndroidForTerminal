package by.liscode.inventory

import by.liscode.domain.model.StatusOrder


sealed interface CurrentOrderScreenAction {
    class TakeMarker(val marker: String) : CurrentOrderScreenAction
    class UpdateOrder(val statusOrder: StatusOrder):CurrentOrderScreenAction
    class UpdateActivityScanner(val isActive: Boolean): CurrentOrderScreenAction
}