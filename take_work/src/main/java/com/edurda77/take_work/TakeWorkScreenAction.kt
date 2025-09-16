package com.edurda77.take_work

sealed interface TakeWorkScreenAction {
    class TakeMarker(val marker:String):TakeWorkScreenAction
    data object UpdateStatusToWork:TakeWorkScreenAction
    data object ClearMarkers:TakeWorkScreenAction
}