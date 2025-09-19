package ru.modulmoney.current_order

import by.liscode.domain.model.StatusOrder


sealed interface CurrentOrderScreenAction {
    class TakeMarker(val marker: String) : CurrentOrderScreenAction
    class UpdateOrder(val statusOrder: StatusOrder):CurrentOrderScreenAction
    class UpdateActivityScanner(val isActive: Boolean): CurrentOrderScreenAction
}