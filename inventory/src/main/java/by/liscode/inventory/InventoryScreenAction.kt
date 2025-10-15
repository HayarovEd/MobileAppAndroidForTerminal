package by.liscode.inventory

import by.liscode.domain.model.TypeLinen

sealed interface InventoryScreenAction {
    data object SaveResult: InventoryScreenAction
    class UpdateActivityScanner(val isActive: Boolean): InventoryScreenAction
    class TakeMarker(
        val marker:String,
        val sound: Int
    ):InventoryScreenAction
    class UpdateFilter(val filterByType: FilterByType):InventoryScreenAction
    class UpdateCurrentType(val typeLinen: TypeLinen):InventoryScreenAction
    data object UpdateGrouping:InventoryScreenAction
    class UpdateInventoryState(val inventoryState: InventoryState):InventoryScreenAction

    data object CloseSuccessDialog:InventoryScreenAction
}