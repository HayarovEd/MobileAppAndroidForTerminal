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
        Log.d("REST TERMINAL", "power ${rfidSetting.power}")
        Log.d("REST TERMINAL", "frequencyMode ${rfidSetting.frequencyMode.value}")
        Log.d("REST TERMINAL", "protocol ${rfidSetting.protocol.value}")
        Log.d("REST TERMINAL", "attribute ${rfidSetting.attribute.value}")
        power = (rfidSetting.power).roundToInt()
        frequencyMode = rfidSetting.frequencyMode.value
      /*  epcAndTIDUserMode = InventoryModeEntity
            .Builder()
            .setMode(InventoryModeEntity.MODE_EPC_TID)
            .build()*/
        protocol = 0x00//rfidSetting.protocol.value
        setFilter(rfidSetting.attribute.value, 0, 0, "")
        Log.d("REST TERMINAL", "get power $power")
        Log.d("REST TERMINAL", "get frequencyMode $frequencyMode")
        Log.d("REST TERMINAL", "get protocol $protocol")
        Log.d("REST TERMINAL", "get epcAndTIDUserMode ${epcAndTIDUserMode.mode}")
    }
}

fun processScan(
    isActive: Boolean,
    rfid: RFIDWithUHFUART,
    block: (String) -> Unit,
    updateIsActive: (Boolean) -> Unit,
) {
    with(rfid) {
    }
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