package by.liscode.domain.model

data class RfidSetting(
    val standard: RfidProtocol,
    val attribute: RfidAttribute,
    val power: Int,
    val frequencyMode: FrequencyMode,
)
