package by.liscode.inventory

import androidx.lifecycle.viewModelScope
import by.liscode.domain.model.GroupLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import by.liscode.domain.repository.LocalRepository
import by.liscode.domain.repository.RemoteRepository
import by.liscode.domain.repository.SoundRepository
import by.liscode.domain.utils.InfoMessage
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

class InventoryScreenViewModel(
    dataStoreRepository: DataStoreRepository,
    localRepository: LocalRepository,
    jwtRepository: JwtRepository,
    soundRepository: SoundRepository,
    private val remoteRepository: RemoteRepository,
) : BaseViewModel(
    dataStoreRepository = dataStoreRepository,
    jwtRepository = jwtRepository,
    localRepository = localRepository,
    soundRepository = soundRepository,
) {

    private val mutex = Mutex()
    private val _state = MutableStateFlow(InventoryScreenState())
    val state = _state
        .onStart {
            loadData (
                action = { token, url ->
                getLinens(
                    url = url,
                    token = token
                )
            },
                navigate = {
                    viewModelScope.launch {
                        _eventFlow.emit(UiInventoryEvents.LoginNavigationEvent)
                    }
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = InventoryScreenState()
        )

    private val _eventFlow = MutableSharedFlow<UiInventoryEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: InventoryScreenAction) {
        when (action) {
            InventoryScreenAction.SaveResult -> {
                loadData (
                    action = { token, url ->
                        sendResults(
                            url = url,
                            accessToken = token
                        )
                    },
                    navigate = {
                        viewModelScope.launch {
                            _eventFlow.emit(UiInventoryEvents.LoginNavigationEvent)
                        }
                    }
                )
            }
            is InventoryScreenAction.TakeMarker -> {
                viewModelScope.launch {
                    mutex.withLock {
                        takeMarker(
                            marker = action.marker,
                            sound = action.sound,
                            isUpdate = !state.value.markers.contains(action.marker)&&state.value.remoteLinens.map { it.rfid }.contains(action.marker),
                            actionAddMarker = {
                                _state.value.copy(
                                    markers = state.value.markers + action.marker
                                )
                                    .updateState()
                            }
                        )
                    }
                }

            }
            is InventoryScreenAction.UpdateActivityScanner -> {
                _state.value.copy(
                    isActiveScanner = action.isActive,
                ).updateState()
                if (action.isActive) {
                    _state.value.copy(
                        markers = emptyList(),
                    ).updateState()
                }
                updateMessage(if (action.isActive) InfoMessage.RFIDWork.STARTED.asText() else
                    InfoMessage.RFIDWork.STOPPED.asText())
            }
            is InventoryScreenAction.UpdateFilter -> {
                _state.value.copy(
                    filterByType = action.filterByType
                )
                    .updateState()
            }
            InventoryScreenAction.UpdateGrouping -> {
                _state.value.copy(
                   groupLinen = when (state.value.groupLinen) {
                       GroupLinen.NAME -> GroupLinen.TYPE_LINEN
                       GroupLinen.TYPE_LINEN -> GroupLinen.NAME
                   }
                )
                    .updateState()
            }

            is InventoryScreenAction.UpdateInventoryState -> {
                _state.value.copy(
                    inventoryState = action.inventoryState
                )
                    .updateState()
            }

            is InventoryScreenAction.UpdateCurrentType -> {
                _state.value.copy(
                    currentTypeLinen = action.typeLinen
                )
                    .updateState()
            }

            InventoryScreenAction.CloseSuccessDialog -> {
                _state.value.copy(
                    isShowSuccessDialog = false
                )
                    .updateState()
            }
        }
    }

    private fun getLinens(
        url: String,
        token: String,
    ) {
        _state.value.copy(
            isLoading = true
        )
            .updateState()
        viewModelScope.launch {
            when (val result = remoteRepository.getLinens(
                baseUrl = url,
                token = token
            )) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        isLoading = false,
                    )
                        .updateState()
                    updateMessage(result.error.asUiText())
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        isLoading = false,
                        remoteLinens = result.data.filter { it.status == StatusLinen.IN_STOCK }
                    )
                        .updateState()
                }
            }
        }
    }

    private fun sendResults(
        accessToken: String,
        url: String
    ) {
        viewModelScope.launch {
            _state.value.copy(
                isLoading = true,
            )
                .updateState()
            when (val result = remoteRepository.sendResultInventory(
                baseUrl = url,
                token = accessToken,
                rfids = state.value.markers.filter { marker-> marker in state.value.remoteLinens.map { it.rfid } }
            )) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        isLoading = false,
                    )
                        .updateState()
                    updateMessage(result.error.asUiText())
                }
                is ResultWork.Success -> {
                    _state.value.copy(
                        isLoading = false,
                        isShowSuccessDialog = true
                    )
                        .updateState()
                }
            }
        }
    }

    private fun InventoryScreenState.updateState() {
        _state.update {
            this
        }
    }
}