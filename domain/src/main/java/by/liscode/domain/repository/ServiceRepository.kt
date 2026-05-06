package by.liscode.domain.repository

import by.liscode.domain.model.DeviceInfo

interface ServiceRepository {
    fun getDeviceInfo(): DeviceInfo
}