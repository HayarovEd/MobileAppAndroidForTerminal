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
    Log.d("REST TERMINAL", "frequencyMode ${rfidSetting.frequencyMode.value}")
    Log.d("REST TERMINAL", "protocol ${rfidSetting.protocol.value}")
    Log.d("REST TERMINAL", "power ${rfidSetting.power}")
    Log.d("REST TERMINAL", "attribute ${rfidSetting.attribute.value}")
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
    Log.d("REST TERMINAL", "scanner power ${rfid.power}")
    Log.d("REST TERMINAL", "scanner protocol ${rfid.protocol}")
    Log.d("REST TERMINAL", "scanner frequencyMode ${rfid.frequencyMode}")
   // rfid.setFilter(2, 0, 0, "300ED89F335000800147159D")
    if (isActive) {
        rfid.stopInventory()
        //rfid.free()
        updateIsActive(false)
        Log.d("REST TERMINAL", "scanner stop")
    } else {
        Log.d("REST TERMINAL", "scanner start")
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
    Log.d("REST TERMINAL", "scanner power ${rfid.power}")
    Log.d("REST TERMINAL", "scanner protocol ${rfid.protocol}")
    Log.d("REST TERMINAL", "scanner frequencyMode ${rfid.frequencyMode}")
    // rfid.setFilter(2, 0, 0, "300ED89F335000800147159D")
    when (scannerWork) {
        ScannerWork.CLEAR -> {
            Log.d("REST TERMINAL", "scanner start")
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
            Log.d("REST TERMINAL", "scanner stop")
        }
        ScannerWork.STOP -> {
            Log.d("REST TERMINAL", "scanner clear")
            blockClear()
            updateScannerWork(ScannerWork.CLEAR)
        }
    }
}