package by.liscode.write_off

import by.liscode.domain.model.Filter
import by.liscode.domain.model.Linen
import by.liscode.domain.model.ReasonWriteOff
import by.liscode.domain.model.RfidSetting
import by.liscode.resources.uikit.UiText

data class WriteOffScreenState(
    val settings: RfidSetting? = null,
    val message: UiText? = null,
    val isLoading: Boolean = false,
    val markers: List<String> = emptyList(),
    val remoteLinens: List<Linen> = emptyList(),
    val reasonWriteOff: ReasonWriteOff = ReasonWriteOff.DAMAGED,
    val actWriteOff: String = "",
    val isShowInfo: Boolean = true,
    val isShowDialog: Boolean = false,
    val filterMarkers: List<Filter> = emptyList(),
    val isActiveScanner: Boolean = false,
    val reasons: List<ReasonWriteOff> = ReasonWriteOff.entries,
    val isShowSuccessDialog: Boolean = false,
)

sealed class UiWriteOffEvents {
    data object LoginNavigationEvent : UiWriteOffEvents()
}
