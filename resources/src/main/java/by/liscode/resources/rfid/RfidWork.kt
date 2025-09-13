package by.liscode.resources.rfid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import by.liscode.domain.model.RfidSetting
import by.liscode.resources.uikit.OnScan
import by.liscode.resources.uikit.rememberRfid
import by.liscode.resources.utils.applySettings
import by.liscode.resources.utils.processScan

@Composable
fun RfidWork(
    settings: RfidSetting?,
    onTakeMarker: (String) -> Unit,
) {
    val context = LocalContext.current
    val scanner =  rememberRfid()?.apply { init(context) }
    var isActive by remember { mutableStateOf(false) }
    LaunchedEffect(settings) {
        settings?.let {
            scanner?.applySettings(it)
        }
    }
    scanner?.let {
    OnScan(
        block = {
            processScan(
                rfid = scanner,
                isActive = isActive,
                block = {
                    onTakeMarker(it)
                },
                updateIsActive = {
                    isActive = it
                }
            )
        }
    )}
}