package by.liscode.domain.model

import by.liscode.domain.utils.ETSI_FREQ
import by.liscode.domain.utils.RUSSIA_FREQ

enum class FrequencyMode(
    val title: String,
    val value: Int,
) {
    RUSSIA(
        title = RUSSIA_FREQ,
        value = 37
    ),
    ETSI(
        title = ETSI_FREQ,
        value = 4
    )
}