package ru.modulmoney.order_create

import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import by.liscode.domain.repository.LocalRepository
import by.liscode.domain.repository.RemoteRepository
import by.liscode.resources.model.ScannerWork
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

class OrderCreateScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val localRepository: LocalRepository,
    jwtRepository: JwtRepository,
    private val remoteRepository: RemoteRepository,
) : BaseViewModel(
    dataStoreRepository = dataStoreRepository,
    jwtRepository = jwtRepository
) {


    private val _state = MutableStateFlow(OrderCreateScreenState())
    val state = _state
        .onStart {
            getLocalData()
            loadData(
                action = { token, url ->
                    getRemoteData(
                        url = url,
                        accessToken = token
                    )
                },
                navigate = {
                    viewModelScope.launch {
                        _eventFlow.emit(UiOrderCreateEvents.LoginNavigationEvent)
                    }
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = OrderCreateScreenState()
        )
    private val _eventFlow = MutableSharedFlow<UiOrderCreateEvents>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun onAction(action: OrderCreateScreenAction) {
        when (action) {
            OrderCreateScreenAction.CreateOrder -> {
                createOrder()
            }

            is OrderCreateScreenAction.TakeMarker -> {
                if (state.value.filterMarkers.isEmpty()) {
                    if (!state.value.markers.contains(action.marker)) {
                        _state.value.copy(
                            markers = state.value.markers + action.marker
                        )
                            .updateState()
                        loadData(
                            action = { token, url ->
                                getLinenById(
                                    rfid = action.marker, accessToken = token, url = url
                                )
                            },
                            navigate = {
                                viewModelScope.launch {
                                    _eventFlow.emit(UiOrderCreateEvents.LoginNavigationEvent)
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
                                    rfid = action.marker, accessToken = token, url = url
                                )
                            },
                            navigate = {
                                viewModelScope.launch {
                                    _eventFlow.emit(UiOrderCreateEvents.LoginNavigationEvent)
                                }
                            }
                        )
                    }
                }
            }

            is OrderCreateScreenAction.UpdateActivityScanner -> {
                _state.value.copy(
                    scannerWork = action.scannerWork
                )
                    .updateState()
                if (action.scannerWork == ScannerWork.CLEAR) {
                    _state.value.copy(
                        markers = emptyList(),
                        remoteLinens = emptyList()
                    )
                        .updateState()
                }
            }

            is OrderCreateScreenAction.UpdateLaundry -> {
                _state.value.copy(
                    currentLaundry = action.laundry
                )
                    .updateState()
            }

            OrderCreateScreenAction.CreateOrderWithNotApprovedMarkers -> {
                createOrderWithAllLinens()
            }

            OrderCreateScreenAction.CloseWarning -> {
                _state.value.copy(
                    isShowWarning = false
                ).updateState()
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

    private fun getRemoteData(
        accessToken: String,
        url: String
    ) {
        _state.value.copy(
            isLoading = true
        )
            .updateState()
        viewModelScope.launch {
            when (val result = remoteRepository.getEmployees(
                baseUrl = url,
                token = accessToken
            )) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        message = result.error.asUiText(),
                        isLoading = false
                    )
                        .updateState()
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        laundries = result.data,
                        isLoading = false
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


    private fun createOrder() {
        _state.value.copy(
            isLoading = true
        )
            .updateState()
        viewModelScope.launch {
            if (state.value.notApprovedLinens.isEmpty()) {
                //TODO - send state.value.approvedLines
                _state.value.copy(
                    isLoading = false
                )
                    .updateState()
            } else {
                _state.value.copy(
                    isShowWarning = true
                ).updateState()
            }
        }
    }

    private fun createOrderWithAllLinens() {
        _state.value.copy(
            isLoading = true,
            isShowWarning = false
        )
            .updateState()
        viewModelScope.launch {
            //TODO - send state.value.remoteLines
            _state.value.copy(
                isLoading = false
            )
                .updateState()
        }
    }

    private fun OrderCreateScreenState.updateState() {
        _state.update {
            this
        }
    }
}