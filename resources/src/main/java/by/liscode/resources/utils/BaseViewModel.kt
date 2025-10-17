package by.liscode.resources.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import by.liscode.domain.repository.LocalRepository
import by.liscode.resources.uikit.UiText
import by.liscode.resources.uikit.asUiText
import com.nine.statio.domain.utils.ResultWork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

abstract class BaseViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val jwtRepository: JwtRepository,
    private val localRepository: LocalRepository,
): ViewModel() {

    private val _baseState = MutableStateFlow(BaseState())
    val baseState = _baseState
        .onStart {
            getLocalData()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = BaseState()
        )


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

    protected fun getLocalData() {
        viewModelScope.launch {
            localRepository.getAllFilters().collect {
                when (it) {
                    is ResultWork.Error -> {
                        _baseState.value.copy(
                            message = it.error.asUiText()
                        )
                            .updateState()
                    }

                    is ResultWork.Success -> {
                        _baseState.value.copy(
                            filterMarkers = it.data
                        )
                            .updateState()
                    }
                }
            }
        }
        viewModelScope.launch {
            dataStoreRepository.getRfidSettings().collect {
                _baseState.value.copy(
                    settings = it
                )
                    .updateState()
            }
        }
    }

    protected fun updateMessage(message: UiText?) {
        _baseState.value.copy(
            message = message
        )
            .updateState()
    }

    private fun BaseState.updateState() {
        _baseState.update {
            this
        }
    }
}