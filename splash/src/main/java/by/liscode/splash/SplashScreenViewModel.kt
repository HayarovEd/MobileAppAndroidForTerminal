package by.liscode.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SplashScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val jwtRepository: JwtRepository
) : ViewModel() {


    init {
        loadData()
    }

    private val _eventFlow = MutableSharedFlow<UiSplashEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    @OptIn(ExperimentalTime::class)
    private fun loadData() {
        viewModelScope.launch {
            delay(2000)
            dataStoreRepository.getBearer().collect { userToken ->
                if (userToken!=null) {
                    val expired = jwtRepository.decodeJwt(userToken)?.expired
                    expired?.let { exp ->
                        if (exp < Clock.System.now().epochSeconds) {
                            dataStoreRepository.removeBearer()
                            _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
                        } else {
                            _eventFlow.emit(UiSplashEvents.FundNavigateEvent)
                        }
                    } ?: {
                        viewModelScope.launch {
                            dataStoreRepository.removeBearer()
                            _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
                        }
                    }
                } else  {
                viewModelScope.launch {
                    _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
                     }
                }
            }
        }
    }
}