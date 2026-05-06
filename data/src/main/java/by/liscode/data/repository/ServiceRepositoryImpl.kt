package by.liscode.data.repository

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import by.liscode.domain.model.DeviceInfo
import by.liscode.domain.repository.ServiceRepository

class ServiceRepositoryImpl(private val application: Application) : ServiceRepository {

    override fun getDeviceInfo(): DeviceInfo {
        val versionOs = Build.VERSION.SDK_INT.toString()
        val versionApp = getAppVersionCode()
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return DeviceInfo(
            versionOs = versionOs,
            versionApp = versionApp,
            manufacturer = manufacturer,
            model = model
        )
    }

    private fun getAppVersionCode(): Long {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                application.packageManager.getPackageInfo(application.packageName, 0)
            } else {
                application.packageManager.getPackageInfo(application.packageName, 0)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            0
        }
    }
}