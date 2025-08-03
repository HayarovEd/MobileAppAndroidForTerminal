package by.liscode.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import by.liscode.domain.model.FrequencyMode
import by.liscode.domain.model.RfidAttribute
import by.liscode.domain.model.RfidProtocol
import by.liscode.domain.model.RfidSetting
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.utils.LABEL_ATTRIBUTE
import by.liscode.domain.utils.LABEL_FREQ_MODE
import by.liscode.domain.utils.LABEL_POWER
import by.liscode.domain.utils.LABEL_PROTOCOL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>
): DataStoreRepository {

    override suspend fun saveRfidSettings(
        rfidSetting: RfidSetting
    ) {
        dataStore.edit { preferences ->
            preferences[FIELD_LABEL_PROTOCOL] = rfidSetting.standard.value
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
               standard = when (preferences[FIELD_LABEL_PROTOCOL]) {
                   0 -> RfidProtocol.ISO180006C
                  // 3 -> RfidProtocol.ISO180006D
                   else -> RfidProtocol.ISO180006C
               },
               attribute = when (preferences[FIELD_LABEL_ATTRIBUTE]) {
                   1 -> RfidAttribute.EPC
                   2 -> RfidAttribute.TID
                   else -> RfidAttribute.EPC
               },
               power = preferences[FIELD_LABEL_POWER]?:0.1f,
               frequencyMode = when (preferences[FIELD_LABEL_FREQ_MODE]) {
                   0x04 -> FrequencyMode.ETSI
                   0x37 -> FrequencyMode.RUSSIA
                   else -> FrequencyMode.RUSSIA
               }
           )
       }
    }

    companion object {
        val FIELD_LABEL_PROTOCOL = intPreferencesKey(LABEL_PROTOCOL)
        val FIELD_LABEL_ATTRIBUTE = intPreferencesKey(LABEL_ATTRIBUTE)
        val FIELD_LABEL_POWER = floatPreferencesKey(LABEL_POWER)
        val FIELD_LABEL_FREQ_MODE = intPreferencesKey(LABEL_FREQ_MODE)
    }
}