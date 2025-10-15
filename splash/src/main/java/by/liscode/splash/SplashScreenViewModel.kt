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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
        _state.value.copy(
            isLoading = true
        )
            .updateState()
        viewModelScope.launch {
            delay(2000)
            dataStoreRepository.getBearer().collect { userToken ->
                if (userToken!=null) {
                    val expired = jwtRepository.decodeJwt(userToken)?.expired
                    expired?.let { exp ->
                        if (exp < Clock.System.now().epochSeconds) {
                            dataStoreRepository.removeBearer()
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
                            dataStoreRepository.removeBearer()
                            _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
                            _state.value.copy(
                                isLoading = false
                            )
                                .updateState()
                        }
                    }
                } else  {
                viewModelScope.launch {
                    _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
                     }
                    _state.value.copy(
                        isLoading = false
                    )
                        .updateState()
                }
            }
        }
    }

    private fun SplashState.updateState() {
        _state.update {
            this
        }
    }
}