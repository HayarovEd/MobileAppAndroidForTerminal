package com.abelfma.take_balance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import by.liscode.domain.repository.LocalRepository
import by.liscode.domain.repository.RemoteRepository
import by.liscode.resources.uikit.asUiText
import com.nine.statio.domain.utils.ResultWork
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TakeBalanceScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val localRepository: LocalRepository,
    private val jwtRepository: JwtRepository,
    private val remoteRepository: RemoteRepository,
) : ViewModel() {


    private val _state = MutableStateFlow(TakeBalanceScreenState())
    val state = _state
        .onStart {
            getLocalData()
            loadData { token, url ->
                getRemoteData(
                    accessToken = token,
                    url = url
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TakeBalanceScreenState()
        )

    private val _eventFlow = MutableSharedFlow<UiTakeBalanceEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

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

    fun onAction(action: TakeBalanceScreenAction) {
        when (action) {
            is TakeBalanceScreenAction.AddNewCounterParty -> {
                loadData { token, url ->
                    addCounterparty(
                        accessToken = token,
                        url = url,
                        name = action.name,
                        address = action.address,
                        inn = action.inn,
                        phone = action.phone
                    )
                }
            }

            is TakeBalanceScreenAction.ChangeStateScreen -> {
                _state.value.copy(
                    stateScreen = action.stateScreen
                )
                    .updateState()
            }

            TakeBalanceScreenAction.OpenInfo -> {
                _state.value.copy(
                    isOpenInfo = !state.value.isOpenInfo
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.SearchCounterParty -> {
                _state.value.copy(
                    queryCounterparty = action.query
                )
                    .updateState()
            }

            TakeBalanceScreenAction.Send -> {
                TODO()
            }

            is TakeBalanceScreenAction.SetArticleNumber -> {
                _state.value.copy(
                    articleNumber = action.number
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.SetBillLading -> {
                _state.value.copy(
                    billLading = action.billLading
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.SetCounterParty -> {
                _state.value.copy(
                    currentCounterparty = action.counterparty
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.SetStateLinen -> {
                _state.value.copy(
                    currentStateLinen = action.stateLinen
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.SetTypeLinen -> {
                _state.value.copy(
                    currentTypeLinen = action.typeLinen
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.SetWeight -> {
                _state.value.copy(
                    weight = action.weight
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.TakeMarker -> {
                if (state.value.filterMarkers.isEmpty()) {
                    if (!state.value.markers.contains(action.marker)) {
                        Log.d("REST TERMINAL", "saved marker no filter ${action.marker}")
                        _state.value.copy(
                            markers = state.value.markers + action.marker
                        )
                            .updateState()
                    }
                } else {
                    if (state.value.filterMarkers.any {
                            action.marker.contains(
                                it.query,
                                ignoreCase = true
                            )
                        } && !state.value.markers.contains(action.marker)) {
                        Log.d("REST TERMINAL", "saved marker with filter ${action.marker}")
                        _state.value.copy(
                            markers = state.value.markers + action.marker
                        )
                            .updateState()
                    }
                }
            }

            is TakeBalanceScreenAction.RemoveMarker -> {
                _state.value.copy(
                    markers = state.value.markers - action.marker
                )
                    .updateState()
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun loadData(action: (String, String) -> Unit) {
        viewModelScope.launch {
            dataStoreRepository.getBearer().collect { userToken ->
                userToken?.let { token ->
                    val expired = jwtRepository.decodeJwt(token)?.expired
                    expired?.let { exp ->
                        if (exp < Clock.System.now().epochSeconds) {
                            dataStoreRepository.removeBearer()
                            _eventFlow.emit(UiTakeBalanceEvents.LoginNavigationEvent)
                        } else {
                            dataStoreRepository.getUrl().collect { url ->
                                if (url.isNotBlank()) {
                                    action(token, url)
                                }
                            }
                        }
                    } ?: {
                        viewModelScope.launch {
                            dataStoreRepository.removeBearer()
                            _eventFlow.emit(UiTakeBalanceEvents.LoginNavigationEvent)
                        }
                    }
                } ?: {
                    viewModelScope.launch {
                        dataStoreRepository.removeBearer()
                        _eventFlow.emit(UiTakeBalanceEvents.LoginNavigationEvent)
                    }
                }
            }
        }
    }

    private fun getRemoteData(
        accessToken: String,
        url: String
    ) {
        _state.value.copy(
            isLoading = true
        )
            .updateState()
        viewModelScope.launch {
            val counterpartiesResultDiff = async {
                remoteRepository.getCounterparties(
                    token = accessToken,
                    baseUrl = url
                )
            }
            val statesLinenResultDiff = async {
                remoteRepository.getStatesLinen(
                    token = accessToken,
                    baseUrl = url
                )
            }
            /* val statusesResultDiff = async {
                 remoteRepository.getStatusLinen(
                     token = accessToken,
                     baseUrl = url
                 )
             }*/
            when (val result = counterpartiesResultDiff.await()) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        message = result.error.asUiText()
                    )
                        .updateState()
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        counterparties = result.data
                    )
                        .updateState()
                }
            }
            when (val result = statesLinenResultDiff.await()) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        message = result.error.asUiText()
                    )
                        .updateState()
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        statesLinen = result.data
                    )
                        .updateState()
                }
            }
            /*when (val result = statusesResultDiff.await()) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        message = result.error.asUiText()
                    )
                        .updateState()
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        sta = result.data
                    )
                        .updateState()
                }
            }*/
            _state.value.copy(
                isLoading = false
            )
                .updateState()
        }
    }

    private fun addCounterparty(
        accessToken: String,
        url: String,
        name: String,
        address: String,
        inn: String,
        phone: String,
    ) {
        _state.value.copy(
            isLoading = true
        )
            .updateState()
        viewModelScope.launch {
            when (val result = remoteRepository.addCounterparty(
                baseUrl = url,
                token = accessToken,
                name = name,
                address = address,
                inn = inn,
                phone = phone
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
                        counterparties = state.value.counterparties + result.data
                    )
                        .updateState()
                }
            }
        }
    }

    private fun TakeBalanceScreenState.updateState() {
        _state.update {
            this
        }
    }
}