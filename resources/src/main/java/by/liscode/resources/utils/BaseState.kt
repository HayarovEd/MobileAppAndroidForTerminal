package by.liscode.resources.utils

import by.liscode.domain.model.Filter
import by.liscode.domain.model.RfidSetting
import by.liscode.resources.uikit.UiText

data class BaseState(
    val settings: RfidSetting? = null,
    val filterMarkers: List<Filter> = emptyList(),
    val message: UiText? = null,
)
