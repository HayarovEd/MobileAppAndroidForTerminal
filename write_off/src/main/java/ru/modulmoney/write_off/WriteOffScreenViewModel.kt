package ru.modulmoney.write_off

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class WriteOffScreenViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(WriteOffScreenState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = WriteOffScreenState()
        )

    fun onAction(action: WriteOffScreenAction) {
        when (action) {
            else -> TODO("Handle actions")
        }
    }

    private fun WriteOffScreenState.updateState() {
        _state.update {
            this
        }
    }
}