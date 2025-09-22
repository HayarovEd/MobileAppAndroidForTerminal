package by.liscode.take_balance

import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Filter
import by.liscode.domain.model.RfidSetting
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.resources.uikit.UiText

data class TakeBalanceScreenState(
    val isLoading: Boolean = false,
    val message: UiText? = null,
    val settings: RfidSetting? = null,
    val markers: List<String> = emptyList(),
    val statesLinen: List<StateLinen> = StateLinen.entries, // release from remote
    val typesLinen: List<TypeLinen> = emptyList(),
    val counterparties: List<Counterparty> = emptyList(),
    val isOpenInfo: Boolean = true,
    val currentTypeLinen: TypeLinen? = null,
    val articleNumber: String = "",
    val currentStateLinen: StateLinen = StateLinen.NEW,
    val weight: String = "",
    //val price: String = "",
    val billLading: String = "",
    val currentCounterparty: Counterparty? = null,
    val stateScreen: StateScreen = StateScreen.ADD,
    val queryCounterparty: String = "",
    val filterMarkers: List<Filter> = emptyList(),
    val isShowSuccessDialog: Boolean = false,
) {
    val searchedCounterParties = counterparties.filter { it.name.contains(queryCounterparty, ignoreCase = true) }
}

enum class StateScreen{
    ADD,
    COUNTERPARTY
}

sealed class UiTakeBalanceEvents {
    data object LoginNavigationEvent : UiTakeBalanceEvents()
}
