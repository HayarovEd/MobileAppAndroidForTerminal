package by.liscode.inventory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import by.liscode.domain.model.StatusOrder
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import by.liscode.domain.repository.LocalRepository
import by.liscode.domain.repository.RemoteRepository
import by.liscode.domain.repository.SoundRepository
import by.liscode.domain.utils.InfoMessage
import by.liscode.resources.model.NavigationRoute
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

class CurrentOrderScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val localRepository: LocalRepository,
    private val jwtRepository: JwtRepository,
    private val remoteRepository: RemoteRepository,
    private val soundRepository: SoundRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(
    dataStoreRepository = dataStoreRepository,
    jwtRepository = jwtRepository
) {
    private val orderId = savedStateHandle.toRoute<NavigationRoute.CurrentOrder>().id

    private val _state = MutableStateFlow(CurrentOrderScreenState())
    val state = _state
        .onStart {
            getLocalData()
            loadData(
                action = { token, url ->
                    getOrder(
                        url = url,
                        token = token
                    )
                },
                navigate = {
                    viewModelScope.launch {
                        _eventFlow.emit(UiCurrentOrderEvents.LoginNavigationEvent)
                    }
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CurrentOrderScreenState()
        )

    private val _eventFlow = MutableSharedFlow<UiCurrentOrderEvents>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun onAction(action: CurrentOrderScreenAction) {
        when (action) {
            is CurrentOrderScreenAction.TakeMarker -> {
                if (state.value.filterMarkers.isEmpty()) {
                    if (!state.value.markers.contains(action.marker)) {
                        soundRepository.playSound(action.sound)
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
                        soundRepository.playSound(action.sound)
                        _state.value.copy(
                            markers = state.value.markers + action.marker
                        )
                            .updateState()
                    }
                }
            }

            is CurrentOrderScreenAction.UpdateActivityScanner -> {
                _state.value.copy(
                    isActiveScanner = action.isActive,
                    message = if (action.isActive) InfoMessage.RFIDWork.STARTED.asText() else
                        InfoMessage.RFIDWork.STOPPED.asText()
                ).updateState()
            }

            is CurrentOrderScreenAction.UpdateOrder -> {
                state.value.order?.let {
                    when (it.status) {
                        StatusOrder.REGISTERED, StatusOrder.IN_TRANSIT -> {
                            loadData(
                                action = { token, url ->
                                    takeOrderToProduction(
                                        url = url,
                                        token = token
                                    )
                                },
                                navigate = {
                                    viewModelScope.launch {
                                        _eventFlow.emit(UiCurrentOrderEvents.LoginNavigationEvent)
                                    }
                                }
                            )
                        }

                        StatusOrder.IN_PRODUCTION -> {
                            loadData(
                                action = { token, url ->
                                    takeOrderFromProduction(
                                        url = url,
                                        token = token
                                    )
                                },
                                navigate = {
                                    viewModelScope.launch {
                                        _eventFlow.emit(UiCurrentOrderEvents.LoginNavigationEvent)
                                    }
                                }
                            )
                        }

                        StatusOrder.FORMED, StatusOrder.IN_DELIVERY -> {
                            loadData(
                                action = { token, url ->
                                    takeOrderToStock(
                                        url = url,
                                        token = token
                                    )
                                },
                                navigate = {
                                    viewModelScope.launch {
                                        _eventFlow.emit(UiCurrentOrderEvents.LoginNavigationEvent)
                                    }
                                }
                            )
                        }

                        StatusOrder.COMPLETED -> {}
                    }
                }
            }

            CurrentOrderScreenAction.CloseSuccessDialog -> {
                _state.value.copy(
                    visibilitySuccessDialog = VisibilitySuccessDialog.INVISIBLE
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


    private fun getOrder(
        url: String,
        token: String,
    ) {
        _state.value.copy(
            isLoading = true
        )
            .updateState()
        viewModelScope.launch {
            when (val result = remoteRepository.getOrderById(
                baseUrl = url,
                token = token,
                id = orderId
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
                        order = result.data
                    )
                        .updateState()
                }
            }
        }
    }

    private fun takeOrderToProduction(
        url: String,
        token: String,
    ) {
        state.value.order?.let { order ->
            _state.value.copy(
                isLoading = true
            )
                .updateState()
            viewModelScope.launch {
                when (val result = remoteRepository.acceptOrderInUse(
                    baseUrl = url,
                    token = token,
                    orderId = order.id,
                    rfids = order.items.map { it.rfid }
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
                            order = result.data,
                            visibilitySuccessDialog = VisibilitySuccessDialog.SUCCESS_INTO_USE
                        )
                            .updateState()
                    }
                }
            }
        }
    }

    private fun takeOrderFromProduction(
        url: String,
        token: String,
    ) {
        state.value.order?.let { order ->
            _state.value.copy(
                isLoading = true
            )
                .updateState()
            viewModelScope.launch {
                when (val result = remoteRepository.formOrderAfterProduction(
                    baseUrl = url,
                    token = token,
                    orderId = order.id,
                    rfids = order.items.map { it.rfid }
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
                            order = result.data,
                            visibilitySuccessDialog = VisibilitySuccessDialog.SUCCESS_AFTER_USE
                        )
                            .updateState()
                    }
                }
            }
        }
    }

    private fun takeOrderToStock(
        url: String,
        token: String,
    ) {
        state.value.order?.let { order ->
            _state.value.copy(
                isLoading = true
            )
                .updateState()
            viewModelScope.launch {
                when (val result = remoteRepository.acceptOrderInToStock(
                    baseUrl = url,
                    token = token,
                    orderId = order.id,
                    rfids = order.items.map { it.rfid }
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
                            order = result.data,
                            visibilitySuccessDialog = VisibilitySuccessDialog.SUCCESS_INTO_STOCK
                        )
                            .updateState()
                    }
                }
            }
        }
    }

    private fun CurrentOrderScreenState.updateState() {
        _state.update {
            this
        }
    }
}