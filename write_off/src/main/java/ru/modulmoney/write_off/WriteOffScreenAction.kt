package ru.modulmoney.write_off

import by.liscode.domain.model.ReasonWriteOff

sealed interface WriteOffScreenAction {
    data object SendWriteOff:WriteOffScreenAction
    data object UpdateVisibilityInfo:WriteOffScreenAction
    data object CloseVisibilityDialog:WriteOffScreenAction
    data object RemoveAndCloseVisibilityDialog:WriteOffScreenAction
    class UpdateReasonWriteOff(val reasonWriteOff: ReasonWriteOff): WriteOffScreenAction
    class UpdateActWriteOff(val act: String): WriteOffScreenAction
    class TakeMarker(val marker:String):WriteOffScreenAction
    class RemoveMarker(val marker:String):WriteOffScreenAction
    class UpdateActivityScanner(val isActive: Boolean): WriteOffScreenAction
}