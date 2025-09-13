package ru.modulmoney.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.model.GroupLinen
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

class InventoryScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val localRepository: LocalRepository,
    private val jwtRepository: JwtRepository,
    private val remoteRepository: RemoteRepository,
) : ViewModel() {


    private val _state = MutableStateFlow(InventoryScreenState())
    val state = _state
        .onStart {
            getLocalData()
            loadData { token, url ->
                getLinens(
                    url = url,
                    token = token
                )
            }
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
                //TODO
            }
            is InventoryScreenAction.TakeMarker -> {
                if (state.value.markers.isEmpty()) {
                    if (!state.value.markers.contains(action.marker)) {
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
                        _state.value.copy(
                            markers = state.value.markers + action.marker
                        )
                            .updateState()
                    }
                }
            }
            is InventoryScreenAction.UpdateActivityScanner -> {
                _state.value.copy(
                    isActiveScanner = action.isActive
                ).updateState()
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
                            _eventFlow.emit(UiInventoryEvents.LoginNavigationEvent)
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
                            _eventFlow.emit(UiInventoryEvents.LoginNavigationEvent)
                        }
                    }
                } ?: {
                    viewModelScope.launch {
                        dataStoreRepository.removeBearer()
                        _eventFlow.emit(UiInventoryEvents.LoginNavigationEvent)
                    }
                }
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
                        message = result.error.asUiText()
                    )
                        .updateState()
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        isLoading = false,
                        remoteLinens = result.data
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