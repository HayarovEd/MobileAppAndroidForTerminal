package by.liscode.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.AuthorizeRepository
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import by.liscode.domain.utils.ResultWork
import by.liscode.resources.uikit.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class LoginScreenViewModel(
    private val authorizeRepository: AuthorizeRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val jwtRepository: JwtRepository
) : ViewModel() {


    private val _state = MutableStateFlow(LoginScreenState())
    val state = _state
        .onStart {
            getBaseUrl()
            getTerminalBearer()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LoginScreenState()
        )

    private val _eventFlow = Channel<UiLoginEvents>()
    val eventFlow = _eventFlow.receiveAsFlow()

    fun onAction(action: LoginScreenAction) {
        when (action) {
            LoginScreenAction.OnLogin -> {
                onLogin()
            }

            LoginScreenAction.UpdateCounter -> {
                updateConter()
            }

            is LoginScreenAction.UpdateSelectedEmployee -> {
                _state.value.copy(
                    selectedEmployee = action.employee
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
                _eventFlow.send(UiLoginEvents.RegisterNavigateEvent)
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
            _state.value.selectedEmployee?.let {
                _state.value.terminalBearer?.let { br->
                    _state.value.copy(
                        isLoading = true
                    )
                        .updateState()
                    when (val result = authorizeRepository.authorizationEmployee(
                        token = br,
                        pin = state.value.password,
                        employeeId = it.id,
                        baseUrl = state.value.baseUrl
                    )) {
                        is ResultWork.Error -> {
                            _state.value.copy(
                                isLoading = false,
                            )
                                .updateState()
                            _eventFlow.send(UiLoginEvents.OnMessage(result.error.asUiText()))
                        }

                        is ResultWork.Success -> {
                            dataStoreRepository.saveBearer(result.data.token)
                            _state.value.copy(
                                isLoading = false
                            )
                                .updateState()
                            _eventFlow.send(UiLoginEvents.FundNavigateEvent)
                        }
                    }
                }
            }
        }
    }

    private fun getTerminalBearer() {
        viewModelScope.launch {
            dataStoreRepository.getTermianlBearer().collect { collector ->
                if (collector.isNullOrBlank()) {
                    _state.value.copy(
                        typeTerminalBearer = TypeTerminalBearer.EMPTY
                    )
                        .updateState()
                } else {
                    val expiredTerminal = jwtRepository.decodeJwt(collector)?.expired
                    expiredTerminal?.let { expT ->
                        if (expT < Clock.System.now().epochSeconds) {
                            _state.value.copy(
                                typeTerminalBearer = TypeTerminalBearer.EXPIRED
                            )
                                .updateState()
                        } else {
                            _state.value.copy(
                                typeTerminalBearer = TypeTerminalBearer.OK,
                                terminalBearer = collector
                            )
                                .updateState()
                            getEmployees(collector)
                        }
                    } ?: {
                        viewModelScope.launch {
                            _state.value.copy(
                                typeTerminalBearer = TypeTerminalBearer.EMPTY
                            )
                                .updateState()
                        }
                    }
                }
            }
        }
    }

    private fun getEmployees(token: String) {
        viewModelScope.launch {
            _state.value.copy(
                isLoading = true
            )
                .updateState()
            when (val result = authorizeRepository.getEmployees(
                token = token,
                baseUrl = state.value.baseUrl
            )) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        isLoading = false,
                    )
                        .updateState()
                    _eventFlow.send(UiLoginEvents.OnMessage(result.error.asUiText()))
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        isLoading = false,
                        employees = result.data
                    )
                        .updateState()
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

