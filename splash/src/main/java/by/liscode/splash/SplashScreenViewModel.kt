package by.liscode.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SplashScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val jwtRepository: JwtRepository
) : ViewModel() {


    private val _state = MutableStateFlow(SplashState())
    val state = _state
        .onStart {
            loadData()
            generateUuid()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SplashState()
        )

    private val _eventFlow = MutableSharedFlow<UiSplashEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    @OptIn(ExperimentalTime::class)
    private fun loadData() {
        viewModelScope.launch {
            _state.value.copy(
                isLoading = true
            )
                .updateState()
            delay(2000)
            combine(dataStoreRepository.getBearer(), dataStoreRepository.getTermianlBearer()) {bearer, terminalBearer->
                Pair(bearer, terminalBearer)
            }.collect { collector->
                if (!collector.first.isNullOrBlank()) {
                    val expired = jwtRepository.decodeJwt(collector.first!!)?.expired
                    expired?.let { exp ->
                        if (exp < Clock.System.now().epochSeconds) {
                            dataStoreRepository.removeBearer()
                            _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
                            _state.value.copy(
                                isLoading = false
                            )
                                .updateState()
                        } else {
                            if (collector.second.isNullOrBlank()) {
                                _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
                                _state.value.copy(
                                    isLoading = false
                                )
                                    .updateState()
                            } else {
                                val expiredTerminal = jwtRepository.decodeJwt(collector.second!!)?.expired
                                expiredTerminal?.let { expT->
                                    if (expT < Clock.System.now().epochSeconds) {
                                        _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
                                        _state.value.copy(
                                            isLoading = false
                                        )
                                            .updateState()
                                    } else {
                                        _eventFlow.emit(UiSplashEvents.FundNavigateEvent)
                                        _state.value.copy(
                                            isLoading = false
                                        )
                                            .updateState()
                                    }
                                } ?: {
                                    viewModelScope.launch {
                                        _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
                                        _state.value.copy(
                                            isLoading = false
                                        )
                                            .updateState()
                                    }
                                }
                            }
                        }
                    } ?: {
                        viewModelScope.launch {
                            dataStoreRepository.removeBearer()
                            _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
                            _state.value.copy(
                                isLoading = false
                            )
                                .updateState()
                        }
                    }
                } else {
                    viewModelScope.launch {
                        _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
                    }
                    _state.value.copy(
                        isLoading = false
                    )
                        .updateState()
                }
            }
            /*dataStoreRepository.getBearer().collect { userToken ->

            }*/
        }
    }

    private fun generateUuid() {
        viewModelScope.launch {
            if (dataStoreRepository.getStateUuid().isNullOrBlank()) {
                val uuid = UUID.randomUUID().toString()
                dataStoreRepository.saveUuid(uuid)
            }
        }
    }

    private fun SplashState.updateState() {
        _state.update {
            this
        }
    }
}