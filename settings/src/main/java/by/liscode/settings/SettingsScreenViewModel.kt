package by.liscode.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.model.RfidSetting
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.LocalRepository
import by.liscode.resources.uikit.asUiText
import com.nine.statio.domain.utils.ResultWork
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val localRepository: LocalRepository,
) : ViewModel() {


    private val _state = MutableStateFlow(SettingsScreenState())
    val state = _state
        .onStart {
            loadSettingsData()
            loadFilters()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingsScreenState()
        )

    private val _eventFlow = MutableSharedFlow<UiSettingsEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: SettingsScreenAction) {
        when (action) {
            SettingsScreenAction.CompleteUpdatePower -> {
                actionUpdateSetting {
                    viewModelScope.launch {
                        dataStoreRepository.saveRfidSettings(
                            rfidSetting = it
                        )
                    }
                }
            }

            SettingsScreenAction.DeleteFilters -> {
                viewModelScope.launch {
                    localRepository.deleteAllFilters()
                }
            }

            SettingsScreenAction.InsertFilter -> {
                if (state.value.filters.size < 5) {
                    viewModelScope.launch {
                        when (val result = localRepository.inserFilter(state.value.query)) {
                            is ResultWork.Error -> {
                                _state.value.copy(
                                    message = result.error.asUiText()
                                )
                                    .updateState()
                            }

                            is ResultWork.Success -> {
                                _state.value.copy(
                                    query = ""
                                )
                                    .updateState()
                            }
                        }
                    }
                }
            }

            is SettingsScreenAction.UpdateAttribute -> {
                actionUpdateSetting {
                    viewModelScope.launch {
                        dataStoreRepository.saveRfidSettings(
                            rfidSetting = it.copy(attribute = action.rfidAttribute)
                        )
                    }
                }
            }

            is SettingsScreenAction.UpdateFrequencyMode -> {
                actionUpdateSetting {
                    viewModelScope.launch {
                        dataStoreRepository.saveRfidSettings(
                            rfidSetting = it.copy(frequencyMode = action.frequencyMode)
                        )
                    }
                }
            }

            is SettingsScreenAction.UpdatePower -> {
                actionUpdateSetting {
                    viewModelScope.launch {
                        dataStoreRepository.saveRfidSettings(
                            rfidSetting = it.copy(power = action.value)
                        )
                    }
                    _state.value.copy(
                        rfidSetting = it.copy(power = action.value)
                    )
                        .updateState()
                }
            }

            is SettingsScreenAction.UpdateQuery -> {
                _state.value.copy(
                    query = action.query
                )
                    .updateState()
            }

            is SettingsScreenAction.UpdateStandard -> {
                actionUpdateSetting {
                    viewModelScope.launch {
                        dataStoreRepository.saveRfidSettings(
                            rfidSetting = it.copy(protocol = action.rfidProtocol)
                        )
                    }
                }
            }

            SettingsScreenAction.Logout -> {
                viewModelScope.launch {
                    dataStoreRepository.removeBearer()
                    _eventFlow.emit(UiSettingsEvents.LoginNavigateEvent)
                }
            }
        }
    }

    private fun loadSettingsData() {
        viewModelScope.launch {
            dataStoreRepository.getRfidSettings().collect { collector ->
                _state.value.copy(
                    rfidSetting = collector
                )
                    .updateState()
            }
        }
    }

    private fun loadFilters() {
        viewModelScope.launch {
            localRepository.getAllFilters().collect { collector ->
                when (collector) {
                    is ResultWork.Error -> {
                        _state.value.copy(
                            message = collector.error.asUiText()
                        )
                            .updateState()
                    }

                    is ResultWork.Success -> {
                        _state.value.copy(
                            filters = collector.data
                        )
                            .updateState()
                    }
                }
            }
        }
    }

    private fun actionUpdateSetting(action: (RfidSetting) -> Unit) {
        state.value.rfidSetting?.let {
            action(it)
        }
    }

    private fun SettingsScreenState.updateState() {
        _state.update {
            this
        }
    }
}