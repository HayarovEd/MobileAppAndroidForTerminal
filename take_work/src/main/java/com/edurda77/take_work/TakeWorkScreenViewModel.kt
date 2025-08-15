package com.edurda77.take_work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.LocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TakeWorkScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val localRepository: LocalRepository,
) : ViewModel() {


    private val _state = MutableStateFlow(TakeWorkScreenState())
    val state = _state
        .onStart {
            getRfidSettings()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TakeWorkScreenState()
        )

    fun onAction(action: TakeWorkScreenAction) {
        when (action) {
            is TakeWorkScreenAction.TakeMarker -> {
                if (!state.value.markers.contains(action.marker)) {
                    _state.value.copy(
                        markers = state.value.markers + action.marker
                    )
                        .updateState()
                }
            }
        }
    }

    private fun getRfidSettings() {
        viewModelScope.launch {
            dataStoreRepository.getRfidSettings().collect { settings ->
                _state.value.copy(
                    settings = settings
                )
                    .updateState()
            }
        }
    }


    private fun TakeWorkScreenState.updateState() {
        _state.update {
            this
        }
    }
}