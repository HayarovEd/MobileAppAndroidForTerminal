package by.liscode.domain.model

data class RfidSetting(
    val protocol: RfidProtocol,
    val attribute: RfidAttribute,
    val power: Float,
    val frequencyMode: FrequencyMode,
)
