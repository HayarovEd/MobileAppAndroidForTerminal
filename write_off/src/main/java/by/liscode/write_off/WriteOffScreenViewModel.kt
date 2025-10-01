package by.liscode.write_off

import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import by.liscode.domain.repository.LocalRepository
import by.liscode.domain.repository.RemoteRepository
import by.liscode.resources.uikit.asUiText
import by.liscode.resources.utils.BaseViewModel
import com.nine.statio.domain.utils.ResultWork
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WriteOffScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val localRepository: LocalRepository,
    jwtRepository: JwtRepository,
    private val remoteRepository: RemoteRepository,
) : BaseViewModel(
    dataStoreRepository = dataStoreRepository,
    jwtRepository = jwtRepository
) {


    private val _state = MutableStateFlow(WriteOffScreenState())
    val state = _state.onStart {
        getLocalData()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = WriteOffScreenState()
    )

    private val _eventFlow = MutableSharedFlow<UiWriteOffEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: WriteOffScreenAction) {
        when (action) {
            WriteOffScreenAction.CloseVisibilityDialog -> {
                _state.value.copy(
                    isShowDialog = false
                ).updateState()
            }

            is WriteOffScreenAction.RemoveMarker -> {
                removeMarker(action.marker)
            }

            WriteOffScreenAction.SendWriteOff -> {
                checkWriteOff()
            }

            is WriteOffScreenAction.TakeMarker -> {
                if (state.value.filterMarkers.isEmpty()) {
                    if (!state.value.markers.contains(action.marker)) {
                        _state.value.copy(
                            markers = state.value.markers + action.marker
                        ).updateState()
                        loadData(
                            action = { token, url ->
                                getLinenById(
                                    rfid = action.marker, accessToken = token, url = url
                                )
                            },
                            navigate = {
                                viewModelScope.launch {
                                    _eventFlow.emit(UiWriteOffEvents.LoginNavigationEvent)
                                }
                            }
                        )
                    }
                } else {
                    if (state.value.filterMarkers.any {
                            action.marker.contains(
                                it.query, ignoreCase = true
                            )
                        } && !state.value.markers.contains(action.marker)) {
                        _state.value.copy(
                            markers = state.value.markers + action.marker
                        ).updateState()
                        loadData(
                            action = { token, url ->
                                getLinenById(
                                    rfid = action.marker, accessToken = token, url = url
                                )
                            },
                            navigate = {
                                viewModelScope.launch {
                                    _eventFlow.emit(UiWriteOffEvents.LoginNavigationEvent)
                                }
                            }
                        )
                    }
                }
            }

            is WriteOffScreenAction.UpdateActWriteOff -> {
                _state.value.copy(
                    actWriteOff = action.act
                ).updateState()
            }

            is WriteOffScreenAction.UpdateReasonWriteOff -> {
                _state.value.copy(
                    reasonWriteOff = action.reasonWriteOff
                ).updateState()
            }

            WriteOffScreenAction.UpdateVisibilityInfo -> {
                _state.value.copy(
                    isShowInfo = !state.value.isShowInfo
                ).updateState()
            }

            WriteOffScreenAction.RemoveAndCloseVisibilityDialog -> {
                removeAndSend()
            }

            is WriteOffScreenAction.UpdateActivityScanner -> {
                _state.value.copy(
                    isActiveScanner = action.isActive
                ).updateState()
            }

            WriteOffScreenAction.CloseSuccessDialog -> {
                _state.value.copy(
                    isShowSuccessDialog = false
                )
                    .updateState()
            }
        }
    }


    private fun getLocalData() {
        viewModelScope.launch {
            localRepository.getAllFilters().collect {
                when (it) {
                    is ResultWork.Error -> {
                        _state.value.copy(
                            message = it.error.asUiText()
                        ).updateState()
                    }

                    is ResultWork.Success -> {
                        _state.value.copy(
                            filterMarkers = it.data
                        ).updateState()
                    }
                }
            }
        }
        viewModelScope.launch {
            dataStoreRepository.getRfidSettings().collect {
                _state.value.copy(
                    settings = it
                ).updateState()
            }
        }
    }

    private fun getLinenById(
        rfid: String, accessToken: String, url: String
    ) {
        viewModelScope.launch {
            when (val result = remoteRepository.getLinenByRfid(
                baseUrl = url, token = accessToken, rfid = rfid
            )) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        message = result.error.asUiText()
                    ).updateState()
                }

                is ResultWork.Success -> {
                    if (result.data !in state.value.remoteLinens) {
                        _state.value.copy(
                            remoteLinens = state.value.remoteLinens + result.data
                        ).updateState()
                    }
                }
            }
        }
    }

    private fun removeMarker(marker: String) {
        _state.value.copy(
            markers = state.value.markers - marker,
            remoteLinens = state.value.remoteLinens.filter { it.rfid != marker }).updateState()

    }


    private fun checkWriteOff() {
        if (state.value.remoteLinens.size != state.value.markers.size) {
            _state.value.copy(
                isShowDialog = true
            ).updateState()
        } else {
            sendWriteOff(
                state.value.remoteLinens.map { it.rfid }
            )
        }
    }

    private fun removeAndSend() {
        val writeOffMarkers = state.value.markers.filter { marker -> marker in state.value.remoteLinens.map { it.rfid } }
        _state.value.copy(
            markers = writeOffMarkers,
            isShowDialog = false
        ).updateState()
        sendWriteOff(
            writeOffMarkers
        )
    }

    private fun sendWriteOff(
        writeOffMarkers: List<String>
    ) {
        loadData(
            action = { token, url ->
                _state.value.copy(
                    isLoading = true
                ).updateState()
                viewModelScope.launch {
                    when (val result = remoteRepository.writeOffLinens(
                        rfids = writeOffMarkers,
                        baseUrl = url,
                        token = token,
                        reason = state.value.reasonWriteOff,
                        comment = state.value.actWriteOff
                    )) {
                        is ResultWork.Error -> {
                            _state.value.copy(
                                isLoading = false,
                                message = result.error.asUiText()
                            ).updateState()
                        }

                        is ResultWork.Success -> {
                            _state.value.copy(
                                isLoading = false,
                                isShowSuccessDialog = true,
                                remoteLinens = result.data
                            ).updateState()
                        }
                    }
                }
            },
            navigate = {
                viewModelScope.launch {
                    _eventFlow.emit(UiWriteOffEvents.LoginNavigationEvent)
                }
            })
    }


    private fun WriteOffScreenState.updateState() {
        _state.update {
            this
        }
    }
}