package by.liscode.search

import by.liscode.domain.model.TypeLinen

sealed interface SearchScreenAction {
    class UpdateActivityScanner(val isActive: Boolean): SearchScreenAction
    class TakeMarker(
        val marker:String,
        val sound: Int
    ):SearchScreenAction
    class UpdateFilter(val filterByType: FilterByType):SearchScreenAction
    data object UpdateGroupping:SearchScreenAction
    class UpdateSearchState(val searchState: SearchState):SearchScreenAction
    class UpdateFilterRfid(val rfid: String):SearchScreenAction
    class UpdateCurrentType(val typeLinen: TypeLinen):SearchScreenAction
}