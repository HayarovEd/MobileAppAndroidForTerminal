package com.edurda77.take_work

import androidx.lifecycle.viewModelScope
import by.liscode.domain.model.GroupLinen
import by.liscode.domain.model.StatusLinen
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

class TakeWorkScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val localRepository: LocalRepository,
    jwtRepository: JwtRepository,
    private val remoteRepository: RemoteRepository,
) : BaseViewModel(
    dataStoreRepository = dataStoreRepository,
    jwtRepository = jwtRepository
) {


    private val _state = MutableStateFlow(TakeWorkScreenState())
    val state = _state
        .onStart {
            getLocalData()
        }
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
                if (state.value.filterMarkers.isEmpty()) {
                    if (!state.value.markers.contains(action.marker)) {
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
                } else {
                    if (state.value.filterMarkers.any {
                            action.marker.contains(
                                it.query,
                                ignoreCase = true
                            )
                        } && !state.value.markers.contains(action.marker)) {
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
                }
            }

            TakeWorkScreenAction.UpdateStatusToWork -> {
                loadData(
                    action = { token, url ->
                        sendLinenInUse(
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

            TakeWorkScreenAction.ClearMarkers -> {
                _state.value.copy(
                    markers = emptyList(),
                    remoteLinens = emptyList()
                )
                    .updateState()
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
        }
    }

    private fun getLocalData() {
        viewModelScope.launch {
            localRepository.getAllFilters().collect {
                when (it) {
                    is ResultWork.Error -> {
                        _state.value.copy(
                            message = it.error.asUiText()
                        )
                            .updateState()
                    }

                    is ResultWork.Success -> {
                        _state.value.copy(
                            filterMarkers = it.data
                        )
                            .updateState()
                    }
                }
            }
        }
        viewModelScope.launch {
            dataStoreRepository.getRfidSettings().collect {
                _state.value.copy(
                    settings = it
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
                    _state.value.copy(
                        message = result.error.asUiText()
                    ).updateState()
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        remoteLinens = state.value.remoteLinens + result.data
                    ).updateState()
                }
            }
        }
    }

    private fun sendLinenInUse(
        accessToken: String,
        url: String
    ) {
        _state.value.copy(
            isLoading = true
        ).updateState()
        viewModelScope.launch {
            when (val result = remoteRepository.sendLinenInToUse(
                baseUrl = url,
                token = accessToken,
                rfids = state.value.remoteLinens.filter { it.status == StatusLinen.IN_STOCK }.map { it.rfid }
            )) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        message = result.error.asUiText(),
                        isLoading = false
                    ).updateState()
                }

                is ResultWork.Success -> {
                    val updatedLinens = state.value.remoteLinens.map { linen ->
                        result.data.find { it.id == linen.id } ?: linen
                    }
                    _state.value.copy(
                        isLoading = false,
                        isShowResultDialog = true,
                        remoteLinens = updatedLinens
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