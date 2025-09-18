package by.liscode.resources.utils

import android.util.Log
import by.liscode.domain.model.RfidSetting
import by.liscode.resources.model.ScannerWork
import com.rscja.deviceapi.RFIDWithUHFUART
import com.rscja.deviceapi.entity.InventoryParameter
import kotlin.math.roundToInt

fun RFIDWithUHFUART.applySettings(
    rfidSetting: RfidSetting,
):RFIDWithUHFUART {
    return this.apply {
        power = (rfidSetting.power*100).roundToInt()
        frequencyMode = rfidSetting.frequencyMode.value
        protocol = rfidSetting.protocol.value
        setFilter(rfidSetting.attribute.value, 0, 0, "")
    }
}

fun processScan(
    isActive: Boolean,
    rfid: RFIDWithUHFUART,
    block: (String) -> Unit,
    updateIsActive: (Boolean) -> Unit,
) {
    if (isActive) {
        rfid.stopInventory()
        //rfid.free()
        updateIsActive(false)
    } else {
        updateIsActive(true)
        rfid.setInventoryCallback { uhftagInfo ->
            block(uhftagInfo.epc)
        }
        val inventoryParameter = InventoryParameter()
        if (rfid.startInventoryTag(inventoryParameter)) {
            Log.d("REST TERMINAL", "startInventoryTag")
        }
    }
}

fun processWorkScan(
    scannerWork: ScannerWork,
    rfid: RFIDWithUHFUART,
    blockScan: (String) -> Unit,
    blockClear: () -> Unit,
    updateScannerWork: (ScannerWork) -> Unit,
) {
    when (scannerWork) {
        ScannerWork.CLEAR -> {
            rfid.setInventoryCallback { uhftagInfo ->
                blockScan(uhftagInfo.epc)
            }
            val inventoryParameter = InventoryParameter()
            if (rfid.startInventoryTag(inventoryParameter)) {
                Log.d("REST TERMINAL", "startInventoryTag")
            }
            updateScannerWork(ScannerWork.SCANNER)
        }
        ScannerWork.SCANNER -> {
            rfid.stopInventory()
            //rfid.free()
            updateScannerWork(ScannerWork.STOP)
        }
        ScannerWork.STOP -> {
            blockClear()
            updateScannerWork(ScannerWork.CLEAR)
        }
    }
}