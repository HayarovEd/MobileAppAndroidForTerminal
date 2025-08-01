package by.liscode.settings

import by.liscode.domain.model.FrequencyMode
import by.liscode.domain.model.RfidAttribute
import by.liscode.domain.model.RfidProtocol

sealed interface SettingsScreenAction {
    class UpdateStandard(val rfidProtocol: RfidProtocol) : SettingsScreenAction
    class UpdateAttribute(val rfidAttribute: RfidAttribute) : SettingsScreenAction
    class UpdatePower(val value: Float) : SettingsScreenAction
    class UpdateFrequencyMode(val frequencyMode: FrequencyMode) : SettingsScreenAction
    data object DeleteFilters : SettingsScreenAction
    data object InsertFilter : SettingsScreenAction
    data object CompleteUpdatePower : SettingsScreenAction
    class UpdateQuery(val query: String) : SettingsScreenAction
    data object Logout : SettingsScreenAction
}