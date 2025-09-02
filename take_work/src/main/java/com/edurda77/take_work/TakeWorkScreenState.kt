package com.edurda77.take_work

import by.liscode.domain.model.Linen
import by.liscode.domain.model.RfidSetting
import by.liscode.resources.uikit.UiText

data class TakeWorkScreenState(
    val settings: RfidSetting? = null,
    val message: UiText? = null,
    val isLoading: Boolean = false,
    val markers: List<String> = emptyList(),
    val remoteLinens: List<Linen> = emptyList(),
)