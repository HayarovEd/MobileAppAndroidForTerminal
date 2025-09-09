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
                userToken?.let { token ->
                    val expired = jwtRepository.decodeJwt(token)?.expired
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
                } ?: {
                    viewModelScope.launch {
                        dataStoreRepository.removeBearer()
                        _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
                    }
                }
            }
        }
    }


    /*private fun gerBearer() {
        viewModelScope.launch {

            dataStoreRepository.getBearer().collect { bearer->
                bearer?.let {

                }?: _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
            }
        }
    }*/
}