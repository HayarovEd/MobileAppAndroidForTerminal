package com.abelfma.take_balance

import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.TypeLinen

sealed interface TakeBalanceScreenAction {
    data object Send:TakeBalanceScreenAction
    data object OpenInfo:TakeBalanceScreenAction
    class SetTypeLinen(val typeLinen: TypeLinen): TakeBalanceScreenAction
    class SetArticleNumber(val number: String):TakeBalanceScreenAction
    class SetStateLinen(val stateLinen: StateLinen): TakeBalanceScreenAction
    class SetWeight(val weight: String): TakeBalanceScreenAction
    class SetBillLading(val billLading: String): TakeBalanceScreenAction
    class SetCounterParty(val counterparty: Counterparty): TakeBalanceScreenAction
    class ChangeStateScreen(val stateScreen: StateScreen): TakeBalanceScreenAction
    class SearchCounterParty(val query: String):TakeBalanceScreenAction
    class AddNewCounterParty(
        val name: String,
        val address: String,
        val inn: String,
        val phone: String,
    ):TakeBalanceScreenAction
    class TakeMarker(val marker:String):TakeBalanceScreenAction
}