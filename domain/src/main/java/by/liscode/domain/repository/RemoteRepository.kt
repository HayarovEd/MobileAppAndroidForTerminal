package by.liscode.domain.repository

import by.liscode.domain.model.Counterparty
import by.liscode.domain.utils.DataError
import com.nine.statio.domain.utils.ResultWork

interface RemoteRepository {
    suspend fun getCounterparties(
        baseUrl: String,
        token: String
    ): ResultWork<List<Counterparty>, DataError>
}