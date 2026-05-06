package by.liscode.order_create

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

class OrderCreateScreenViewModel(
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
    private val _state = MutableStateFlow(OrderCreateScreenState())
    val state = _state
        .onStart {
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
                                            _eventFlow.emit(UiOrderCreateEvents.LoginNavigationEvent)
                                        }
                                    }
                                )
                            }
                        )
                    }
                }

            }

            is OrderCreateScreenAction.UpdateActivityScanner -> {
                _state.value.copy(
                    isActiveScanner = action.isActive,
                )
                    .updateState()
                if (action.isActive) {
                    _state.value.copy(
                        markers = emptyList(),
                        remoteLinens = emptyList()
                    ).updateState()
                }
                updateMessage(if (action.isActive) InfoMessage.RFIDWork.STARTED.asText() else
                    InfoMessage.RFIDWork.STOPPED.asText())
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

            OrderCreateScreenAction.CloseSuccessDialog -> {
                _state.value.copy(
                    isShowSuccessDialog = false
                )
                    .updateState()
            }

            OrderCreateScreenAction.UpdateGrouping -> {
                _state.value.copy(
                    groupLinen = when (state.value.groupLinen) {
                        GroupLinen.NAME -> GroupLinen.TYPE_LINEN
                        GroupLinen.TYPE_LINEN -> GroupLinen.NAME
                    }
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
            when (val result = remoteRepository.getClients(
                baseUrl = url,
                token = accessToken
            )) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        isLoading = false
                    )
                        .updateState()
                    updateMessage(result.error.asUiText())
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
                  // updateMessage(result.error.asUiText(),)
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


    private fun createOrder() {
        if (state.value.notApprovedLinens.isEmpty()) {
            loadData(
                action = { token, url ->
                    createNewOrder(
                        accessToken = token,
                        url = url,
                        rfids = state.value.approvedLinens.map { it.rfid }
                    )
                },
                navigate = {
                    viewModelScope.launch {
                        _eventFlow.emit(UiOrderCreateEvents.LoginNavigationEvent)
                    }
                }
            )
        } else {
            _state.value.copy(
                isShowWarning = true
            ).updateState()
        }
    }

    private fun createOrderWithAllLinens() {
        _state.value.copy(
            isShowWarning = false
        )
            .updateState()
        loadData(
            action = { token, url ->
                createNewOrder(
                    accessToken = token,
                    url = url,
                    rfids = state.value.markers
                )
            },
            navigate = {
                viewModelScope.launch {
                    _eventFlow.emit(UiOrderCreateEvents.LoginNavigationEvent)
                }
            }
        )
    }

    private fun createNewOrder(
        accessToken: String,
        url: String,
        rfids: List<String>,
    ) {
        viewModelScope.launch {
            state.value.currentLaundry?.let { currentLaundry ->
                _state.value.copy(
                    isLoading = true
                )
                    .updateState()
                when (val result = remoteRepository.createOrder(
                    baseUrl = url,
                    token = accessToken,
                    rfids = rfids,
                    laundryId = currentLaundry.id,
                    address = currentLaundry.location
                )) {
                    is ResultWork.Error -> {
                        _state.value.copy(
                            isLoading = false,
                        )
                            .updateState()
                        updateMessage(result.error.asUiText(),)
                    }

                    is ResultWork.Success -> {
                        _state.value.copy(
                            isLoading = false,
                            isShowSuccessDialog = true,
                            order = result.data
                        )
                            .updateState()
                    }
                }
            }
        }
    }

    private fun OrderCreateScreenState.updateState() {
        _state.update {
            this
        }
    }
}