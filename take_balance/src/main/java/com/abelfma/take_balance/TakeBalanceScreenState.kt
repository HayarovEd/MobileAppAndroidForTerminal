package com.abelfma.take_balance

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
   // val url: String = "",
   // val userToken: String? = null,
    val markers: List<String> = emptyList(),
    val statesLinen: List<StateLinen> = StateLinen.entries, // release from remote
    val typesLinen: List<TypeLinen> = emptyList(),
    val counterparties: List<Counterparty> = emptyList(),
    val isOpenInfo: Boolean = true,
    val currentTypeLinen: TypeLinen? = null,
    val articleNumber: String = "",
    val currentStateLinen: StateLinen? = null,
    val weight: String = "",
    //val price: String = "",
    val billLading: String = "",
    val currentCounterparty: Counterparty? = null,
    val stateScreen: StateScreen = StateScreen.ADD,
    val queryCounterparty: String = "",
    val filterMarkers: List<Filter> = emptyList()
)

enum class StateScreen{
    ADD,
    COUNERPARTY
}

sealed class UiTakeBalanceEvents {
    data object LoginNavigationEvent : UiTakeBalanceEvents()
}
