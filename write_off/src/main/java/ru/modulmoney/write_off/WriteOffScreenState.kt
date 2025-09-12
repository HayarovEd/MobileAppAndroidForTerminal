package ru.modulmoney.write_off

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
    val isShowDialog: Boolean = true,
    val filterMarkers: List<Filter> = emptyList()
)

sealed class UiWriteOffEvents {
    data object LoginNavigationEvent : UiWriteOffEvents()
}
