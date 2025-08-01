package by.liscode.domain.repository

import by.liscode.domain.model.RfidSetting
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun getRfidSettings(): Flow<RfidSetting>
    suspend fun saveRfidSettings(rfidSetting: RfidSetting)
}