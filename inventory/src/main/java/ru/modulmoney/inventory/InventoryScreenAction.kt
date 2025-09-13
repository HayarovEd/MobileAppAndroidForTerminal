package ru.modulmoney.inventory

sealed interface InventoryScreenAction {
    data object SaveResult: InventoryScreenAction
    class UpdateActivityScanner(val isActive: Boolean): InventoryScreenAction
    class TakeMarker(val marker:String):InventoryScreenAction
    class UpdateFilter(val filterByType: FilterByType):InventoryScreenAction
    data object UpdateGrouping:InventoryScreenAction
    class UpdateInventoryState(val inventoryState: InventoryState):InventoryScreenAction
}