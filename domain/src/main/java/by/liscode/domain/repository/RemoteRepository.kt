package by.liscode.domain.repository

import by.liscode.domain.model.Client
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Linen
import by.liscode.domain.model.Order
import by.liscode.domain.model.ReasonWriteOff
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.StatusOrder
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.utils.DataError
import com.nine.statio.domain.utils.ResultWork

interface RemoteRepository {
    suspend fun getCounterparties(
        baseUrl: String,
        token: String
    ): ResultWork<List<Counterparty>, DataError>

    suspend fun getStatesLinen(
        baseUrl: String,
        token: String
    ): ResultWork<List<StateLinen>, DataError>

    suspend fun getStatusLinen(
        baseUrl: String,
        token: String
    ): ResultWork<List<StatusLinen>, DataError>

    suspend fun addCounterparty(
        baseUrl: String,
        token: String,
        name: String,
        address: String,
        inn: String,
        phone: String
    ): ResultWork<Counterparty, DataError>

    suspend fun getLinenByRfid(
        baseUrl: String,
        token: String,
        rfid: String
    ): ResultWork<Linen, DataError>

    suspend fun getLinens(baseUrl: String, token: String): ResultWork<List<Linen>, DataError>
    suspend fun getTypesLinen(
        baseUrl: String,
        token: String
    ): ResultWork<List<TypeLinen>, DataError>

    suspend fun getOrderStatuses(
        baseUrl: String,
        token: String
    ): ResultWork<List<StatusOrder>, DataError>

    suspend fun getReasonsWriteOff(
        baseUrl: String,
        token: String
    ): ResultWork<List<ReasonWriteOff>, DataError>


    suspend fun getClients(baseUrl: String, token: String): ResultWork<List<Client>, DataError>
    suspend fun getClientById(
        baseUrl: String,
        token: String,
        id: Int
    ): ResultWork<Client, DataError>

    suspend fun sendLinenInToUse(
        baseUrl: String,
        token: String,
        rfids: List<String>,
    ): ResultWork<List<Linen>, DataError>

    suspend fun getOrders(
        baseUrl: String,
        token: String,
    ): ResultWork<List<Order>, DataError>

    suspend fun getOrderById(
        baseUrl: String,
        token: String,
        id: Int
    ): ResultWork<Order, DataError>

    suspend fun createOrder(
        baseUrl: String,
        token: String,
        rfids: List<String>,
        laundryId: Int,
        address: String,
        sendAct: Boolean = true,
    ): ResultWork<Order, DataError>

    suspend fun acceptOrderInUse(
        baseUrl: String,
        token: String,
        orderId: Int,
        ignoreMissing: Boolean = true,
        rfids: List<String>,
    ): ResultWork<Order, DataError>

    suspend fun formOrderAfterProduction(
        baseUrl: String,
        token: String,
        orderId: Int,
        ignoreMissing: Boolean = true,
        rfids: List<String>,
    ): ResultWork<Order, DataError>

    suspend fun acceptOrderInToStock(
        baseUrl: String,
        token: String,
        orderId: Int,
        ignoreMissing: Boolean = true,
        rfids: List<String>,
    ): ResultWork<Order, DataError>

    suspend fun acceptToLinensInToStock(
        baseUrl: String,
        token: String,
        rfids: List<String>,
        typeId: Int,
        nomenclature: String,
        weight: Int,
        consignment: String,
        counterpartyId: Int
    ): ResultWork<List<Linen>, DataError>

    suspend fun writeOffLinens(
        baseUrl: String,
        token: String,
        rfids: List<String>,
        reason: ReasonWriteOff,
        comment: String
    ): ResultWork<List<Linen>, DataError>
}