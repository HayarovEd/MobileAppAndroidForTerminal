package by.liscode.settings

import by.liscode.domain.model.Filter
import by.liscode.domain.model.RfidSetting
import by.liscode.resources.uikit.UiText

data class SettingsScreenState(
    val message: UiText? = null,
    val rfidSetting: RfidSetting? = null,
    val filters: List<Filter> = emptyList(),
    val query: String = "",
)