package by.liscode.write_off

import by.liscode.domain.model.Linen
import by.liscode.domain.model.ReasonWriteOff

data class WriteOffScreenState(
    val isLoading: Boolean = false,
    val markers: List<String> = emptyList(),
    val remoteLinens: List<Linen> = emptyList(),
    val reasonWriteOff: ReasonWriteOff = ReasonWriteOff.DAMAGED,
    val actWriteOff: String = "",
    val isShowInfo: Boolean = true,
    val isShowDialog: Boolean = false,
    val isActiveScanner: Boolean = false,
    val reasons: List<ReasonWriteOff> = ReasonWriteOff.entries,
    val isShowSuccessDialog: Boolean = false,
) {
    val markersApprovedForWriteOff = markers.filter { marker -> marker in remoteLinens.map { it.rfid } }
    val markersNotApprovedForWriteOff = markers.filter { marker -> marker !in remoteLinens.map { it.rfid } }
}

sealed class UiWriteOffEvents {
    data object LoginNavigationEvent : UiWriteOffEvents()
}
