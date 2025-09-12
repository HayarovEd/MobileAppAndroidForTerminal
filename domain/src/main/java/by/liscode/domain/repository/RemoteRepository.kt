package by.liscode.domain.repository

import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Linen
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
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
}