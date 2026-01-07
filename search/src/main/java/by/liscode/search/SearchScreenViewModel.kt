package by.liscode.search

import androidx.lifecycle.viewModelScope
import by.liscode.domain.model.GroupLinen
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

class SearchScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    localRepository: LocalRepository,
    private val jwtRepository: JwtRepository,
    soundRepository: SoundRepository,
    private val remoteRepository: RemoteRepository,
) : BaseViewModel(
    dataStoreRepository = dataStoreRepository,
    jwtRepository = jwtRepository,
    localRepository = localRepository,
    soundRepository = soundRepository,
) {

    private val mutex = Mutex()
    private val _state = MutableStateFlow(SearchScreenState())
    val state = _state
        .onStart {
            getUserData()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SearchScreenState()
        )
    private val _eventFlow = MutableSharedFlow<UiSearchEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: SearchScreenAction) {
        when (action) {
            is SearchScreenAction.TakeMarker -> {
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
                                            _eventFlow.emit(UiSearchEvents.LoginNavigationEvent)
                                        }
                                    }
                                )
                            }
                        )
                    }
                }

            }

            is SearchScreenAction.UpdateActivityScanner -> {
                _state.value.copy(
                    isActiveScanner = action.isActive,
                ).updateState()
                if (action.isActive) {
                    _state.value.copy(
                        markers = emptyList(),
                        remoteLinens = emptyList(),
                        countUnknownMarkers = 0
                    ).updateState()
                }
                updateMessage(
                    if (action.isActive) InfoMessage.RFIDWork.STARTED.asText() else
                        InfoMessage.RFIDWork.STOPPED.asText()
                )
            }

            is SearchScreenAction.UpdateFilter -> {
                _state.value.copy(
                    filterByType = action.filterByType
                )
                    .updateState()
            }

            SearchScreenAction.UpdateGroupping -> {
                _state.value.copy(
                    groupLinen = when (state.value.groupLinen) {
                        GroupLinen.NAME -> GroupLinen.TYPE_LINEN
                        GroupLinen.TYPE_LINEN -> GroupLinen.NAME
                    }
                )
                    .updateState()
            }

            is SearchScreenAction.UpdateSearchState -> {
                _state.value.copy(
                    searchState = action.searchState
                )
                    .updateState()
            }

            is SearchScreenAction.UpdateFilterRfid -> {
                _state.value.copy(
                    filterRfid = action.rfid
                )
                    .updateState()
            }

            is SearchScreenAction.UpdateCurrentType -> {
                _state.value.copy(
                    currentType = action.typeLinen
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
                    //updateMessage(result.error.asUiText())
                    _state.value.copy(
                        countUnknownMarkers = state.value.countUnknownMarkers + 1
                    ).updateState()
                }

                is ResultWork.Success -> {
                    if (result.data !in state.value.remoteLinens) {
                        _state.value.copy(
                            remoteLinens = state.value.remoteLinens + result.data
                        ).updateState()
                        if (state.value.clients.isEmpty() || result.data.clientId !in state.value.clients.map { it.id }) {
                            getClientById(
                                url = url,
                                accessToken = accessToken,
                                clientId = result.data.clientId
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getClientById(
        accessToken: String,
        url: String,
        clientId: Int
    ) {
        viewModelScope.launch {
            when (val result = remoteRepository.getClientById(
                baseUrl = url,
                token = accessToken,
                id = clientId
            )) {
                is ResultWork.Error -> {
                    // updateMessage(result.error.asUiText())
                }

                is ResultWork.Success -> {
                    if (result.data !in state.value.clients) {
                        _state.value.copy(
                            clients = state.value.clients + result.data
                        ).updateState()

                    }
                }
            }
        }
    }

    private fun getUserData() {
        viewModelScope.launch {
            dataStoreRepository.getBearer().collect { token ->
                token?.let {
                    _state.value.copy(
                        userData = jwtRepository.decodeJwt(token)
                    )
                        .updateState()
                }
            }
        }
    }

    private fun SearchScreenState.updateState() {
        _state.update {
            this
        }
    }
}