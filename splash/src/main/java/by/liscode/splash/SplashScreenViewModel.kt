package by.liscode.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashScreenViewModel(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {


    init {
        gerBearer()
    }

    private val _eventFlow = MutableSharedFlow<UiSplashEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    private fun gerBearer() {
        viewModelScope.launch {
            delay(2000)
            dataStoreRepository.getBearer().collect { bearer->
                bearer?.let {
                    _eventFlow.emit(UiSplashEvents.FundNavigateEvent)
                }?: _eventFlow.emit(UiSplashEvents.LoginNavigateEvent)
            }
        }
    }
}