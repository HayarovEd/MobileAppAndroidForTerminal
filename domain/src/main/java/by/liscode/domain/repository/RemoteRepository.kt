package by.liscode.domain.repository

import by.liscode.domain.model.Counterparty
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
}