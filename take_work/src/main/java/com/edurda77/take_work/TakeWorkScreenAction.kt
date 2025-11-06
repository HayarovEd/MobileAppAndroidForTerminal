package com.edurda77.take_work


sealed interface TakeWorkScreenAction {
    class TakeMarker(
        val marker:String,
        val sound: Int
    ):TakeWorkScreenAction
    data object UpdateStatusToWork:TakeWorkScreenAction
    data object UpdateGrouping:TakeWorkScreenAction
    data object CloseResultDialog:TakeWorkScreenAction
    class UpdateActivityScanner(val isActive: Boolean): TakeWorkScreenAction
    data object CloseWarning:TakeWorkScreenAction
    data object SendAndCloseWarning:TakeWorkScreenAction
}