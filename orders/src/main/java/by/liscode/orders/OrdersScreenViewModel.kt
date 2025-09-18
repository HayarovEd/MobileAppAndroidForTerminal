package by.liscode.orders

import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
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

class OrdersScreenViewModel(
    dataStoreRepository: DataStoreRepository,
    jwtRepository: JwtRepository,
    private val remoteRepository: RemoteRepository,
) : BaseViewModel(
    dataStoreRepository = dataStoreRepository,
    jwtRepository = jwtRepository
){


    private val _state = MutableStateFlow(OrdersScreenState())
    val state = _state
        .onStart {
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
                        message = result.error.asUiText()
                    )
                        .updateState()
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