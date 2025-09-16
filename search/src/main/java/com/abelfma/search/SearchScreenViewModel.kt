package com.abelfma.search

import androidx.lifecycle.viewModelScope
import by.liscode.domain.model.GroupLinen
import by.liscode.domain.repository.AuthorizeRepository
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

class SearchScreenViewModel(
    private val authorizeRepository: AuthorizeRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val localRepository: LocalRepository,
    jwtRepository: JwtRepository,
    private val remoteRepository: RemoteRepository,
) : BaseViewModel(
    dataStoreRepository = dataStoreRepository,
    jwtRepository = jwtRepository
) {
    

    private val _state = MutableStateFlow(SearchScreenState())
    val state = _state
        .onStart {
            getLocalData()
            loadData (
                action = { token, url ->
                    getClientData(
                        url = url,
                        token = token
                    )
                },
                navigate = {
                    viewModelScope.launch {
                        _eventFlow.emit(UiSearchEvents.LoginNavigationEvent)
                    }
                }
            )
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
                if (state.value.markers.isEmpty()) {
                    if (!state.value.markers.contains(action.marker)) {
                        _state.value.copy(
                            markers = state.value.markers + action.marker
                        )
                            .updateState()
                        loadData (
                            action = { token, url ->
                                getLinenById(
                                    rfid = action.marker, accessToken = token, url = url
                                )
                            },
                            navigate = {
                                viewModelScope.launch {
                                    _eventFlow.emit(UiSearchEvents.LoginNavigationEvent)
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
                        loadData (
                            action = { token, url ->
                                getLinenById(
                                    rfid = action.marker, accessToken = token, url = url
                                )
                            },
                            navigate = {
                                viewModelScope.launch {
                                    _eventFlow.emit(UiSearchEvents.LoginNavigationEvent)
                                }
                            }
                        )
                    }
                }
            }
            is SearchScreenAction.UpdateActivityScanner -> {
                _state.value.copy(
                    isActiveScanner = action.isActive
                ).updateState()
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
        }
    }

    /*@OptIn(ExperimentalTime::class)
    private fun loadData(action: (String, String) -> Unit) {
        viewModelScope.launch {
            dataStoreRepository.getBearer().collect { token ->
                if (token!=null) {
                    val expired = jwtRepository.decodeJwt(token)?.expired
                    expired?.let { exp ->
                        if (exp < Clock.System.now().epochSeconds) {
                            dataStoreRepository.removeBearer()
                            _eventFlow.emit(UiSearchEvents.LoginNavigationEvent)
                        } else {
                            dataStoreRepository.getUrl().collect { url ->
                                if (url.isNotBlank()) {
                                    action(token, url)
                                }
                            }
                        }
                    }
                } else {
                    viewModelScope.launch {
                        _eventFlow.emit(UiSearchEvents.LoginNavigationEvent)
                    }
                }
            }
        }
    }*/

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

    private fun getClientData(
        url: String,
        token: String,
    ) {
        _state.value.copy(
            isLoading = true
        )
            .updateState()
        viewModelScope.launch {
            when (val result = authorizeRepository.getProfile(
                baseUrl = url,
                token = token
            )) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        isLoading = false,
                        message = result.error.asUiText()
                    )
                        .updateState()
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        isLoading = false,
                       // client = result.data.f
                    )
                        .updateState()
                }
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
                    _state.value.copy(
                        remoteLinens = state.value.remoteLinens + result.data
                    ).updateState()
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