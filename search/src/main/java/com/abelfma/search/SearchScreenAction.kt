package com.abelfma.search

sealed interface SearchScreenAction {
    class UpdateActivityScanner(val isActive: Boolean): SearchScreenAction
    class TakeMarker(val marker:String):SearchScreenAction
    class UpdateFilter(val filterByType: FilterByType):SearchScreenAction
    data object UpdateGroupping:SearchScreenAction
    class UpdateSearchState(val searchState: SearchState):SearchScreenAction
    class UpdateFilterRfid(val rfid: String):SearchScreenAction
}