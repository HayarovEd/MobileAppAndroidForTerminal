package by.liscode.take_balance

import androidx.lifecycle.viewModelScope
import by.liscode.domain.model.StateLinen
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import by.liscode.domain.repository.LocalRepository
import by.liscode.domain.repository.RemoteRepository
import by.liscode.domain.repository.SoundRepository
import by.liscode.domain.utils.InfoMessage
import by.liscode.resources.uikit.asText
import by.liscode.resources.uikit.asUiText
import by.liscode.resources.utils.BaseViewModel
import com.nine.statio.domain.utils.ResultWork
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TakeBalanceScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val localRepository: LocalRepository,
    jwtRepository: JwtRepository,
    private val remoteRepository: RemoteRepository,
    private val soundRepository: SoundRepository,
) : BaseViewModel(
    dataStoreRepository = dataStoreRepository,
    jwtRepository = jwtRepository,
    localRepository = localRepository
) {

    private val mutex = Mutex()
    private val _state = MutableStateFlow(TakeBalanceScreenState())
    val state = _state
        .onStart {
            loadData(
                action = { token, url ->
                    getRemoteData(
                        url = url,
                        accessToken = token
                    )
                },
                navigate = {
                    viewModelScope.launch {
                        _eventFlow.emit(UiTakeBalanceEvents.LoginNavigationEvent)
                    }
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TakeBalanceScreenState()
        )

    private val _eventFlow = MutableSharedFlow<UiTakeBalanceEvents>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun onAction(action: TakeBalanceScreenAction) {
        when (action) {
            is TakeBalanceScreenAction.AddNewCounterParty -> {
                loadData(
                    action = { token, url ->
                        addCounterparty(
                            accessToken = token,
                            url = url,
                            name = action.name,
                            address = action.address,
                            inn = action.inn,
                            phone = action.phone
                        )
                    },
                    navigate = {
                        viewModelScope.launch {
                            _eventFlow.emit(UiTakeBalanceEvents.LoginNavigationEvent)
                        }
                    }
                )
            }

            is TakeBalanceScreenAction.ChangeStateScreen -> {
                _state.value.copy(
                    stateScreen = action.stateScreen
                )
                    .updateState()
            }

            TakeBalanceScreenAction.OpenInfo -> {
                _state.value.copy(
                    isOpenInfo = !state.value.isOpenInfo
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.SearchCounterParty -> {
                _state.value.copy(
                    queryCounterparty = action.query
                )
                    .updateState()
            }

            TakeBalanceScreenAction.Send -> {
                loadData(
                    action = { token, url ->
                        acceptInToStock(
                            accessToken = token,
                            url = url
                        )
                    },
                    navigate = {
                        viewModelScope.launch {
                            _eventFlow.emit(UiTakeBalanceEvents.LoginNavigationEvent)
                        }
                    }
                )
            }

            is TakeBalanceScreenAction.SetArticleNumber -> {
                _state.value.copy(
                    articleNumber = action.number
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.SetBillLading -> {
                _state.value.copy(
                    billLading = action.billLading
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.SetCounterParty -> {
                _state.value.copy(
                    currentCounterparty = action.counterparty
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.SetStateLinen -> {
                _state.value.copy(
                    currentStateLinen = action.stateLinen
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.SetTypeLinen -> {
                _state.value.copy(
                    currentTypeLinen = action.typeLinen
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.SetWeight -> {
                _state.value.copy(
                    weight = action.weight
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.TakeMarker -> {
                viewModelScope.launch {
                    mutex.withLock {
                        if (baseState.value.filterMarkers.isEmpty()) {
                            if (!state.value.markers.contains(action.marker)) {
                                soundRepository.playSound(action.sound)
                                _state.value.copy(
                                    markers = state.value.markers + action.marker
                                )
                                    .updateState()
                            }
                        } else {
                            if (baseState.value.filterMarkers.any {
                                    action.marker.contains(
                                        it.query,
                                        ignoreCase = true
                                    )
                                } && !state.value.markers.contains(action.marker)) {
                                soundRepository.playSound(action.sound)
                                _state.value.copy(
                                    markers = state.value.markers + action.marker
                                )
                                    .updateState()
                            }
                        }
                    }
                }

            }

            is TakeBalanceScreenAction.RemoveMarker -> {
                _state.value.copy(
                    markers = state.value.markers - action.marker
                )
                    .updateState()
            }

            TakeBalanceScreenAction.CloseSuccessDialog -> {
                _state.value.copy(
                    isShowSuccessDialog = false
                )
                    .updateState()
            }

            is TakeBalanceScreenAction.UpdateActivityScanner -> {
                _state.value.copy(
                    isActiveScanner = action.isActive,
                ).updateState()
                updateMessage(if (action.isActive) InfoMessage.RFIDWork.STARTED.asText() else
                    InfoMessage.RFIDWork.STOPPED.asText())
            }
        }
    }

    private fun getRemoteData(
        accessToken: String,
        url: String
    ) {
        _state.value.copy(
            isLoading = true
        )
            .updateState()
        viewModelScope.launch {
            val counterpartiesResultDiff = async {
                remoteRepository.getCounterparties(
                    token = accessToken,
                    baseUrl = url
                )
            }
            val statesLinenResultDiff = async {
                remoteRepository.getStatesLinen(
                    token = accessToken,
                    baseUrl = url
                )
            }
            val typesLinenDiff = async {
                remoteRepository.getTypesLinen(
                    token = accessToken,
                    baseUrl = url
                )
            }
            when (val result = counterpartiesResultDiff.await()) {
                is ResultWork.Error -> {
                    updateMessage(result.error.asUiText())
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        counterparties = result.data
                    )
                        .updateState()
                }
            }
            when (val result = statesLinenResultDiff.await()) {
                is ResultWork.Error -> {
                    updateMessage(result.error.asUiText())
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        statesLinen = result.data
                    )
                        .updateState()
                }
            }
            when (val result = typesLinenDiff.await()) {
                is ResultWork.Error -> {
                    updateMessage(result.error.asUiText())
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        typesLinen = result.data
                    )
                        .updateState()
                }
            }
            _state.value.copy(
                isLoading = false
            )
                .updateState()
        }
    }

    private fun addCounterparty(
        accessToken: String,
        url: String,
        name: String,
        address: String,
        inn: String,
        phone: String,
    ) {
        _state.value.copy(
            isLoading = true
        )
            .updateState()
        viewModelScope.launch {
            when (val result = remoteRepository.addCounterparty(
                baseUrl = url,
                token = accessToken,
                name = name,
                address = address,
                inn = inn,
                phone = phone
            )) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        isLoading = false,
                    )
                        .updateState()
                    updateMessage(result.error.asUiText())
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        isLoading = false,
                        counterparties = state.value.counterparties + result.data
                    )
                        .updateState()
                }
            }
        }
    }

    private fun acceptInToStock(
        accessToken: String,
        url: String,
    ) {
        if (state.value.currentTypeLinen != null && state.value.currentCounterparty != null) {
            state.value.weight.toIntOrNull()?.let { weight ->
                viewModelScope.launch {
                    _state.value.copy(
                        isLoading = true
                    )
                        .updateState()
                    when (val result = remoteRepository.acceptToLinensInToStock(
                        baseUrl = url,
                        token = accessToken,
                        rfids = state.value.markers,
                        typeId = state.value.currentTypeLinen!!.id,
                        nomenclature = state.value.articleNumber,
                        weight = weight,
                        consignment = state.value.billLading,
                        counterpartyId = state.value.currentCounterparty!!.id
                    )) {
                        is ResultWork.Error -> {
                            _state.value.copy(
                                isLoading = false,
                            )
                                .updateState()
                            updateMessage(result.error.asUiText())
                        }

                        is ResultWork.Success -> {
                            _state.value.copy(
                                isLoading = false,
                                isShowSuccessDialog = true,
                                markers = emptyList(),
                                currentTypeLinen = null,
                                articleNumber = "",
                                currentStateLinen = StateLinen.NEW,
                                weight = "",
                                billLading = "",
                                currentCounterparty = null,
                            )
                                .updateState()
                        }
                    }
                }
            }
        }
    }

    private fun TakeBalanceScreenState.updateState() {
        _state.update {
            this
        }
    }
}