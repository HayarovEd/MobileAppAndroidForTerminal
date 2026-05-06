package by.liscode.regisration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.AuthorizeRepository
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.utils.ResultWork
import by.liscode.resources.uikit.asUiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val authorizeRepository: AuthorizeRepository,
) : ViewModel() {


    private val _state = MutableStateFlow(RegistrationScreenState())
    val state = _state
        .onStart {
            getLocalData()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RegistrationScreenState()
        )

    private val _eventFlow = MutableSharedFlow<UiRegestrationEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: RegistrationScreenAction) {
        when (action) {
            RegistrationScreenAction.RegisterTerminal -> {
                onLogin()
            }

            RegistrationScreenAction.SaveUrl -> {
                viewModelScope.launch {
                    dataStoreRepository.saveUrl(state.value.url)
                }
            }

            is RegistrationScreenAction.UpdateLogin -> {
                _state.value.copy(
                    login = action.login
                )
                    .updateState()
            }

            is RegistrationScreenAction.UpdatePassword -> {
                _state.value.copy(
                    password = action.password
                )
                    .updateState()
            }

            is RegistrationScreenAction.UpdateUrl -> {
                _state.value.copy(
                    url = action.url
                )
                    .updateState()
            }
        }
    }

    private fun onLogin() {
        viewModelScope.launch {
            _state.value.copy(
                isLoading = true
            )
                .updateState()
            when (val result = authorizeRepository.authorization(
                login = state.value.login,
                password = state.value.password,
                baseUrl = state.value.url
            )) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        isLoading = false,
                        message = result.error.asUiText()
                    )
                        .updateState()
                }

                is ResultWork.Success -> {
                    dataStoreRepository.saveTerminalBearer(result.data.token)
                    _state.value.copy(
                        isLoading = false
                    )
                        .updateState()
                    viewModelScope.launch {
                        _eventFlow.emit(UiRegestrationEvents.SuccessRegistrationTerminal)
                    }
                }
            }
        }
    }

    private fun getLocalData() {
        viewModelScope.launch {
            dataStoreRepository.getUrl().collect { collector ->
                _state.value.copy(
                    url = collector
                )
                    .updateState()
            }
        }
    }

    private fun RegistrationScreenState.updateState() {
        _state.update {
            this
        }
    }
}