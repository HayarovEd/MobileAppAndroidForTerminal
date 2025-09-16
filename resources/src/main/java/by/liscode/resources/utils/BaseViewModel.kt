package by.liscode.resources.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

abstract class BaseViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val jwtRepository: JwtRepository,
): ViewModel() {

    @OptIn(ExperimentalTime::class)
    protected fun loadData(
        action: (String, String) -> Unit,
        navigate: () -> Unit
    ) {
        viewModelScope.launch {
            dataStoreRepository.getBearer().collect { token ->
                if (token!=null) {
                    val expired = jwtRepository.decodeJwt(token)?.expired
                    expired?.let { exp ->
                        if (exp < Clock.System.now().epochSeconds) {
                            dataStoreRepository.removeBearer()
                            navigate()
                        } else {
                            dataStoreRepository.getUrl().collect { url ->
                                if (url.isNotBlank()) {
                                    action(token, url)
                                }
                            }
                        }
                    }
                } else {
                    viewModelScope.launch {
                       navigate()
                    }
                }
            }
        }
    }
}