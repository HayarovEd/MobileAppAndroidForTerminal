package com.edurda77.take_work

sealed interface TakeWorkScreenAction {
    class TakeMarker(val marker:String):TakeWorkScreenAction
}