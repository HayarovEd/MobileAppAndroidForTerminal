package by.liscode.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {


    private val _state = MutableStateFlow(LoginScreenState())
    val state = _state
        .onStart {}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LoginScreenState()
        )

    private val _eventFlow = MutableSharedFlow<UiLoginEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: LoginScreenAction) {
        when (action) {
            LoginScreenAction.Login -> {
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

    private fun onLogin() {
        viewModelScope.launch {

        }
    }

    private fun LoginScreenState.updateState() {
        _state.update {
            this
        }
    }
}

