package by.liscode.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.AuthorizeRepository
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.resources.uikit.asUiText
import by.liscode.domain.utils.ResultWork
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginScreenViewModel(
    private val authorizeRepository: AuthorizeRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {


    private val _state = MutableStateFlow(LoginScreenState())
    val state = _state
        .onStart {
            getBaseUrl()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LoginScreenState()
        )

    private val _eventFlow = MutableSharedFlow<UiLoginEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: LoginScreenAction) {
        when (action) {
            LoginScreenAction.OnLogin -> {
                onLogin()
            }
            LoginScreenAction.UpdateCounter -> {
                updateConter()
            }

            is LoginScreenAction.UpdateLogin -> {
                _state.value.copy(
                    login = action.login
                )
                    .updateState()
            }

            is LoginScreenAction.UpdatePassword -> {
                _state.value.copy(
                    password = action.password
                )
                    .updateState()
            }
        }
    }

    private fun updateConter() {
        if (_state.value.counter < 2) {
            _state.value.copy(
                counter = state.value.counter + 1
            )
                .updateState()
        } else {
            _state.value.copy(
                counter = 0
            )
                .updateState()
            viewModelScope.launch {
                _eventFlow.emit(UiLoginEvents.RegisterNavigateEvent)
            }
        }
    }

    private fun getBaseUrl() {
        viewModelScope.launch {
            dataStoreRepository.getUrl().collect {
                _state.value.copy(
                    baseUrl = it
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
                baseUrl = state.value.baseUrl
            )) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        isLoading = false,
                        message = result.error.asUiText()
                    )
                        .updateState()
                }
                is ResultWork.Success -> {
                    dataStoreRepository.saveBearer(result.data.token)
                    _state.value.copy(
                        isLoading = false
                    )
                        .updateState()
                    _eventFlow.emit(UiLoginEvents.FundNavigateEvent)
                }
            }
        }
    }

    private fun LoginScreenState.updateState() {
        _state.update {
            this
        }
    }
}

