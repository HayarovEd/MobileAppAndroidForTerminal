package com.edurda77.take_work

import by.liscode.resources.model.ScannerWork

sealed interface TakeWorkScreenAction {
    class TakeMarker(
        val marker:String,
        val sound: Int
    ):TakeWorkScreenAction
    data object UpdateStatusToWork:TakeWorkScreenAction
    data object UpdateGrouping:TakeWorkScreenAction
    data object CloseResultDialog:TakeWorkScreenAction
    class UpdateActivityScanner(val scannerWork: ScannerWork): TakeWorkScreenAction
}