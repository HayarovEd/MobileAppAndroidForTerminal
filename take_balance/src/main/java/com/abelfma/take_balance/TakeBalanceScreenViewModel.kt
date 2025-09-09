package com.abelfma.take_balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import by.liscode.domain.repository.LocalRepository
import by.liscode.domain.repository.RemoteRepository
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
                getCounterparties(
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
            is TakeBalanceScreenAction.AddNewCounterParty -> TODO()
            is TakeBalanceScreenAction.ChangeStateScreen -> TODO()
            TakeBalanceScreenAction.OpenInfo -> TODO()
            is TakeBalanceScreenAction.SearchCounterParty -> TODO()
            TakeBalanceScreenAction.Send -> TODO()
            is TakeBalanceScreenAction.SetArticleNumber -> TODO()
            is TakeBalanceScreenAction.SetBillLading -> TODO()
            is TakeBalanceScreenAction.SetCounterParty -> TODO()
            is TakeBalanceScreenAction.SetStateLinen -> TODO()
            is TakeBalanceScreenAction.SetTypeLinen -> TODO()
            is TakeBalanceScreenAction.SetWeight -> TODO()
            is TakeBalanceScreenAction.TakeMarker -> TODO()
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
                            _eventFlow.emit(UiTakeBalanceEvents.LoginNavigationEvent)
                        }
                    }
                } ?: {
                    viewModelScope.launch {
                        _eventFlow.emit(UiTakeBalanceEvents.LoginNavigationEvent)
                    }
                }
            }
        }
    }

    private fun getCounterparties(
        accessToken: String,
        url: String
    ) {
        viewModelScope.launch {
            when (val result = remoteRepository.getCounterparties(
                token = accessToken,
                baseUrl = url
            )) {
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
        }
    }

    private fun TakeBalanceScreenState.updateState() {
        _state.update {
            this
        }
    }
}