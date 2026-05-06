package by.liscode.data.repository

import by.liscode.data.handler.handleReadFromDataBase
import by.liscode.data.handler.handleWriteToDataBase
import by.liscode.data.local.AppDatabase
import by.liscode.data.local.FilterEntity
import by.liscode.domain.model.Filter
import by.liscode.domain.repository.LocalRepository
import by.liscode.domain.utils.DataError
import by.liscode.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalRepositoryImpl(
    db: AppDatabase
) : LocalRepository {
    private val dao = db.filterDao

    override suspend fun inserFilter(
        query: String,
    ): ResultWork<Unit, DataError.LocalDateBase> {
        return handleWriteToDataBase {
            dao.insert(
                FilterEntity(
                    query = query
                )
            )
        }
    }



    override suspend fun getAllFilters(): Flow<ResultWork<List<Filter>, DataError.LocalDateBase>> {
        return handleReadFromDataBase {
            dao.getAllFilters().map { filterEntities ->
                filterEntities.map {
                    Filter(
                        id = it.id,
                        query = it.query
                    )
                }
            }
        }
    }

    override suspend fun deleteAllFilters(): ResultWork<Unit, DataError.LocalDateBase> {
        return handleWriteToDataBase {
            dao.clearAll()
        }
    }
}