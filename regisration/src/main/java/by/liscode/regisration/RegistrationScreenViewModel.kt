package by.liscode.regisration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationScreenViewModel(
    private val dataStoreRepository: DataStoreRepository
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

    fun onAction(action: RegistrationScreenAction) {
        when (action) {
            RegistrationScreenAction.RegisterTerminal -> {
                //TODO()
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

    private fun getLocalData() {
        viewModelScope.launch {
            dataStoreRepository.getUrl().collect { collector->
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