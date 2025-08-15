package com.edurda77.take_work

import by.liscode.domain.model.RfidSetting
import by.liscode.resources.uikit.UiText

data class TakeWorkScreenState(
    val settings: RfidSetting? = null,
    val message: UiText? = null,
)