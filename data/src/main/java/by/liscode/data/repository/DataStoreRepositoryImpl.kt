package by.liscode.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import by.liscode.domain.model.FrequencyMode
import by.liscode.domain.model.RfidAttribute
import by.liscode.domain.model.RfidProtocol
import by.liscode.domain.model.RfidSetting
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.utils.BASE_URL
import by.liscode.domain.utils.LABEL_ATTRIBUTE
import by.liscode.domain.utils.LABEL_BEARER
import by.liscode.domain.utils.LABEL_FREQ_MODE
import by.liscode.domain.utils.LABEL_POWER
import by.liscode.domain.utils.LABEL_PROTOCOL
import by.liscode.domain.utils.LABEL_URL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>
): DataStoreRepository {

    override suspend fun saveRfidSettings(
        rfidSetting: RfidSetting
    ) {
        dataStore.edit { preferences ->
            preferences[FIELD_LABEL_PROTOCOL] = rfidSetting.protocol.value
        }
        dataStore.edit { preferences ->
            preferences[FIELD_LABEL_ATTRIBUTE] = rfidSetting.attribute.value
        }

        dataStore.edit { preferences ->
            preferences[FIELD_LABEL_POWER] = rfidSetting.power
        }
        dataStore.edit { preferences ->
            preferences[FIELD_LABEL_FREQ_MODE] = when (rfidSetting.frequencyMode) {
                FrequencyMode.RUSSIA -> 0x37
                FrequencyMode.ETSI -> 0x04
            }
        }
    }


    override suspend fun getRfidSettings(): Flow<RfidSetting> {
       return dataStore.data.map { preferences ->
           RfidSetting(
               protocol = when (preferences[FIELD_LABEL_PROTOCOL]) {
                   0x00 -> RfidProtocol.ISO180006C
                  // 3 -> RfidProtocol.ISO180006D
                   else -> RfidProtocol.ISO180006C
               },
               attribute = when (preferences[FIELD_LABEL_ATTRIBUTE]) {
                   1 -> RfidAttribute.EPC
                   2 -> RfidAttribute.TID
                   else -> RfidAttribute.EPC
               },
               power = preferences[FIELD_LABEL_POWER]?:0.2f,
               frequencyMode = when (preferences[FIELD_LABEL_FREQ_MODE]) {
                   0x04 -> FrequencyMode.ETSI
                   0x37 -> FrequencyMode.RUSSIA
                   else -> FrequencyMode.RUSSIA
               }
           )
       }
    }

    override suspend fun saveBearer(
        bearer: String
    ) {
        dataStore.edit { preferences ->
            preferences[FIELD_LABEL_BEARER] = bearer
        }
    }


    override suspend fun getBearer(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[FIELD_LABEL_BEARER]
        }
    }

    override suspend fun removeBearer() {
        dataStore.edit { preferences ->
            preferences.remove(FIELD_LABEL_BEARER)
        }
    }

    override suspend fun saveUrl(
        url: String
    ) {
        dataStore.edit { preferences ->
            preferences[FIELD_LABEL_LABEL_URL] = url
        }
    }


    override suspend fun getUrl(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[FIELD_LABEL_LABEL_URL]?: BASE_URL
        }
    }

    companion object {
        val FIELD_LABEL_PROTOCOL = intPreferencesKey(LABEL_PROTOCOL)
        val FIELD_LABEL_ATTRIBUTE = intPreferencesKey(LABEL_ATTRIBUTE)
        val FIELD_LABEL_POWER = floatPreferencesKey(LABEL_POWER)
        val FIELD_LABEL_FREQ_MODE = intPreferencesKey(LABEL_FREQ_MODE)
        val FIELD_LABEL_BEARER = stringPreferencesKey(LABEL_BEARER)
        val FIELD_LABEL_LABEL_URL = stringPreferencesKey(LABEL_URL)
    }
}