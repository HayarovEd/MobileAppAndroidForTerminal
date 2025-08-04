package by.liscode.resources.utils

import by.liscode.domain.model.RfidSetting
import com.rscja.deviceapi.RFIDWithUHFUART
import kotlin.math.roundToInt

fun RFIDWithUHFUART.applySettings(
    rfidSetting: RfidSetting,
):RFIDWithUHFUART {
    return this.apply {
        power = (rfidSetting.power*10).roundToInt()
        frequencyMode = rfidSetting.frequencyMode.value
        protocol = rfidSetting.standard.value
        setFilter(rfidSetting.attribute.value, 0, 0, "")
    }
}