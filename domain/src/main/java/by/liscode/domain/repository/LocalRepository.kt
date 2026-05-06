package by.liscode.domain.repository

import by.liscode.domain.model.Filter
import by.liscode.domain.utils.DataError
import by.liscode.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    suspend fun getAllFilters(): Flow<ResultWork<List<Filter>, DataError.LocalDateBase>>
    suspend fun deleteAllFilters(): ResultWork<Unit, DataError.LocalDateBase>
    suspend fun inserFilter(query: String): ResultWork<Unit, DataError.LocalDateBase>
}