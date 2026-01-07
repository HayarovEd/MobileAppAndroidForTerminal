package by.liscode.orders

import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import by.liscode.domain.repository.LocalRepository
import by.liscode.domain.repository.RemoteRepository
import by.liscode.domain.repository.SoundRepository
import by.liscode.resources.uikit.asUiText
import by.liscode.resources.utils.BaseViewModel
import by.liscode.domain.utils.ResultWork
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrdersScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val jwtRepository: JwtRepository,
    private val remoteRepository: RemoteRepository,
    localRepository: LocalRepository,
    soundRepository: SoundRepository,
) : BaseViewModel(
    dataStoreRepository = dataStoreRepository,
    jwtRepository = jwtRepository,
    localRepository = localRepository,
    soundRepository = soundRepository,
){


    private val _state = MutableStateFlow(OrdersScreenState())
    val state = _state
        .onStart {
            getUserData()
            loadData (
                action = { token, url ->
                    getOrders(
                        url = url,
                        token = token
                    )
                },
                navigate = {
                    viewModelScope.launch {
                        _eventFlow.emit(UiOrdersEvents.LoginNavigationEvent)
                    }
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = OrdersScreenState()
        )

    private val _eventFlow = MutableSharedFlow<UiOrdersEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: OrdersScreenAction) {
        when (action) {
            is OrdersScreenAction.UpdateQuery -> {
                _state.value.copy(
                    query = action.query
                )
                    .updateState()
            }
            is OrdersScreenAction.UpdateSorting -> {
                _state.value.copy(
                    sortedOrders = action.sortedOrders
                )
                    .updateState()
            }
        }
    }

    private fun getUserData() {
        viewModelScope.launch {
            dataStoreRepository.getBearer().collect { token->
                token?.let {
                    _state.value.copy(
                        userData = jwtRepository.decodeJwt(token)
                    )
                        .updateState()
                }
            }
        }
    }

    private fun getOrders(
        url: String,
        token: String,
    ) {
        _state.value.copy(
            isLoading = true
        )
            .updateState()
        viewModelScope.launch {
            when (val result = remoteRepository.getOrders(
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
                        orders = result.data
                    )
                        .updateState()
                }
            }
        }
    }

    private fun OrdersScreenState.updateState() {
        _state.update {
            this
        }
    }
}