package by.liscode.data.handler

import by.liscode.domain.utils.DataError
import by.liscode.domain.utils.ResultWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

suspend fun <D> handleWriteToDataBase(data: suspend () -> D): ResultWork<D, DataError.LocalDateBase> {
    return withContext(Dispatchers.IO) {
        try {
            ResultWork.Success(data())

        } catch (e: Exception) {
            e.printStackTrace()
            ResultWork.Error(DataError.LocalDateBase.ERROR_WRITE_DATA)
        }
    }
}

fun <D> handleReadFromDataBase(data: () -> Flow<D>): Flow<ResultWork<D, DataError.LocalDateBase>> {
    return flow<ResultWork<D, DataError.LocalDateBase>> {
        data.invoke().collect { collector ->
            emit(
                ResultWork.Success(collector)
            )
        }
    }.catch {
        emit(
            ResultWork.Error(DataError.LocalDateBase.ERROR_READ_DATA)
        )
    }
}