package com.edurda77.take_work

import androidx.lifecycle.viewModelScope
import by.liscode.domain.model.GroupLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import by.liscode.domain.repository.LocalRepository
import by.liscode.domain.repository.RemoteRepository
import by.liscode.domain.repository.SoundRepository
import by.liscode.domain.utils.InfoMessage
import by.liscode.resources.model.ScannerWork
import by.liscode.resources.uikit.asText
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TakeWorkScreenViewModel(
    dataStoreRepository: DataStoreRepository,
    localRepository: LocalRepository,
    jwtRepository: JwtRepository,
    soundRepository: SoundRepository,
    private val remoteRepository: RemoteRepository,
) : BaseViewModel(
    dataStoreRepository = dataStoreRepository,
    jwtRepository = jwtRepository,
    localRepository = localRepository,
    soundRepository = soundRepository
) {

    private val mutex = Mutex()
    private val _state = MutableStateFlow(TakeWorkScreenState())
    val state = _state
        .onStart {}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TakeWorkScreenState()
        )
    private val _eventFlow = MutableSharedFlow<UiTakeWorkEvents>()
    val eventFlow = _eventFlow.asSharedFlow()
    fun onAction(action: TakeWorkScreenAction) {
        when (action) {
            is TakeWorkScreenAction.TakeMarker -> {
                viewModelScope.launch {
                    mutex.withLock {
                        takeMarker(
                            marker = action.marker,
                            sound = action.sound,
                            isUpdate = !state.value.markers.contains(action.marker),
                            actionAddMarker = {
                                _state.value.copy(
                                    markers = state.value.markers + action.marker
                                )
                                    .updateState()
                                loadData(
                                    action = { token, url ->
                                        getLinenById(
                                            rfid = action.marker,
                                            accessToken = token,
                                            url = url
                                        )
                                    },
                                    navigate = {
                                        viewModelScope.launch {
                                            _eventFlow.emit(UiTakeWorkEvents.LoginNavigationEvent)
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            }

            TakeWorkScreenAction.UpdateStatusToWork -> {
                loadData(
                    action = { token, url ->
                        checkInToUse(
                            accessToken = token,
                            url = url
                        )
                    },
                    navigate = {
                        viewModelScope.launch {
                            _eventFlow.emit(UiTakeWorkEvents.LoginNavigationEvent)
                        }
                    }
                )

            }

            TakeWorkScreenAction.UpdateGrouping -> {
                when (state.value.groupLinen) {
                    GroupLinen.NAME -> {
                        _state.value.copy(
                            groupLinen = GroupLinen.TYPE_LINEN
                        )
                            .updateState()
                    }

                    GroupLinen.TYPE_LINEN -> {
                        _state.value.copy(
                            groupLinen = GroupLinen.NAME
                        )
                            .updateState()
                    }
                }
            }

            TakeWorkScreenAction.CloseResultDialog -> {
                _state.value.copy(
                    isShowResultDialog = false
                )
                    .updateState()
            }

            is TakeWorkScreenAction.UpdateActivityScanner -> {
                _state.value.copy(
                    scannerWork = action.scannerWork,
                )
                    .updateState()
                updateMessage(
                    when (action.scannerWork) {
                        ScannerWork.CLEAR -> null
                        ScannerWork.SCANNER -> InfoMessage.RFIDWork.STARTED.asText()
                        ScannerWork.STOP -> InfoMessage.RFIDWork.STOPPED.asText()
                    }
                )
                if (action.scannerWork == ScannerWork.CLEAR) {
                    _state.value.copy(
                        markers = emptyList(),
                        remoteLinens = emptyList()
                    )
                        .updateState()
                }
            }

            TakeWorkScreenAction.CloseWarning -> {
                _state.value.copy(
                    isShowWarning = false
                ).updateState()
            }

            TakeWorkScreenAction.SendAndCloseWarning -> {
                loadData(
                    action = { token, url ->
                        sendLinenInUse(
                            accessToken = token,
                            url = url,
                            rfids = state.value.remoteLinens.filter { it.status == StatusLinen.IN_STOCK }.map { it.rfid }
                        )
                    },
                    navigate = {
                        viewModelScope.launch {
                            _eventFlow.emit(UiTakeWorkEvents.LoginNavigationEvent)
                        }
                    }
                )
                _state.value.copy(
                    isShowWarning = false
                )
                    .updateState()
            }
        }
    }

    private fun getLinenById(
        rfid: String,
        accessToken: String,
        url: String
    ) {
        viewModelScope.launch {
            when (val result = remoteRepository.getLinenByRfid(
                baseUrl = url,
                token = accessToken,
                rfid = rfid
            )) {
                is ResultWork.Error -> {
                   updateMessage(result.error.asUiText(),)
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

    private fun checkInToUse(
        accessToken: String,
        url: String,
    ) {
        if (state.value.remoteLinens.all { it.status == StatusLinen.IN_STOCK }) {
            sendLinenInUse(
                accessToken = accessToken,
                url = url,
                rfids = state.value.remoteLinens.map { it.rfid }
            )
        } else {
            _state.value.copy(
                isShowWarning = true
            ).updateState()
        }
    }


    private fun sendLinenInUse(
        accessToken: String,
        url: String,
        rfids: List<String>
    ) {
        _state.value.copy(
            isLoading = true
        ).updateState()
        viewModelScope.launch {
            when (val result = remoteRepository.sendLinenInToUse(
                baseUrl = url,
                token = accessToken,
                rfids = rfids
            )) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        isLoading = false
                    ).updateState()
                    updateMessage(result.error.asUiText(),)
                }

                is ResultWork.Success -> {
                    val updatedLinens = state.value.remoteLinens.map { linen ->
                        result.data.find { it.id == linen.id } ?: linen
                    }
                    _state.value.copy(
                        isLoading = false,
                        isShowResultDialog = true,
                        remoteLinens = emptyList(),
                        markers = emptyList()
                    ).updateState()
                }
            }
        }
    }

    private fun TakeWorkScreenState.updateState() {
        _state.update {
            this
        }
    }
}