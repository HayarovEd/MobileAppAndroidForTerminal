package by.liscode.domain.repository

import by.liscode.domain.model.RfidSetting
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun getRfidSettings(): Flow<RfidSetting>
    suspend fun saveRfidSettings(rfidSetting: RfidSetting)
    suspend fun saveBearer(bearer: String)
    suspend fun getBearer(): Flow<String?>
    suspend fun saveUrl(url: String)
    suspend fun getUrl(): Flow<String>
    suspend fun removeBearer()
}