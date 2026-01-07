package by.liscode.data.handler


import by.liscode.domain.utils.DataError
import com.nine.statio.domain.utils.ResultWork
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

suspend fun <D> handleRemoteResponse(data: suspend () -> D): ResultWork<D, DataError> {
    return withContext(Dispatchers.IO) {
        try {
            ResultWork.Success(data())
        } catch (e: ClientRequestException) {
            when (e.response.status.value) {
                400 -> ResultWork.Error(DataError.Network.BAD_REQUEST)
                401 -> ResultWork.Error(DataError.Network.UNAUTHORIZED)
                404 -> ResultWork.Error(DataError.Network.NOT_FOUND)
                else -> ResultWork.Error(DataError.Network.UNKNOWN)
            }
        } catch (e: ServerResponseException) {
            e.printStackTrace()
            ResultWork.Error(DataError.Network.SERVER_ERROR)
        } catch (e: HttpRequestTimeoutException) {
            e.printStackTrace()
            ResultWork.Error(DataError.Network.REQUEST_TIMEOUT)
        } catch (e: UnresolvedAddressException) {
            e.printStackTrace()
            ResultWork.Error(DataError.Network.NO_INTERNET)
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            ResultWork.Error(DataError.Network.NO_CONNECT_SERVER)
        }
        catch (e: JsonConvertException) {
            e.printStackTrace()
            ResultWork.Error(DataError.SerializationError.FORMAT_ERROR)
        } catch (e: Exception) {
            e.printStackTrace()
            ResultWork.Error(DataError.Network.UNKNOWN)
        }
    }
}

suspend fun <T> platformSafeCall(
    execute: suspend () -> HttpResponse,
    handleResponse: suspend (HttpResponse) -> ResultWork<T, DataError>
): ResultWork<T, DataError> {
    return try {
        val response = execute()
        handleResponse(response)
    } catch (e: ClientRequestException) {
        when (e.response.status.value) {
            400 -> ResultWork.Error(DataError.Network.BAD_REQUEST)
            401 -> ResultWork.Error(DataError.Network.UNAUTHORIZED)
            404 -> ResultWork.Error(DataError.Network.NOT_FOUND)
            else -> ResultWork.Error(DataError.Network.UNKNOWN)
        }
    } catch (e: ServerResponseException) {
        e.printStackTrace()
        ResultWork.Error(DataError.Network.SERVER_ERROR)
    } catch (e: HttpRequestTimeoutException) {
        e.printStackTrace()
        ResultWork.Error(DataError.Network.REQUEST_TIMEOUT)
    } catch (e: UnresolvedAddressException) {
        e.printStackTrace()
        ResultWork.Error(DataError.Network.NO_INTERNET)
    } catch (e: UnknownHostException) {
        e.printStackTrace()
        ResultWork.Error(DataError.Network.NO_CONNECT_SERVER)
    }
    catch (e: JsonConvertException) {
        e.printStackTrace()
        ResultWork.Error(DataError.SerializationError.FORMAT_ERROR)
    } catch (e: Exception) {
        e.printStackTrace()
        ResultWork.Error(DataError.Network.UNKNOWN)
    }
}

suspend inline fun <reified T> safeCall(
    noinline execute: suspend () -> HttpResponse
): ResultWork<T, DataError> {
    return platformSafeCall(
        execute = execute
    ) { response ->
        responseToResult(response)
    }
}

suspend inline fun <reified T> responseToResult(response: HttpResponse): ResultWork<T, DataError> {
    return when(response.status.value) {
        in 200..299 -> {
            try {
                ResultWork.Success(response.body<T>())
            } catch(e: NoTransformationFoundException) {
                ResultWork.Error(DataError.SerializationError.FORMAT_ERROR)
            }
        }
        400 -> ResultWork.Error(DataError.Network.BAD_REQUEST)
        401 -> ResultWork.Error(DataError.Network.UNAUTHORIZED)
        403 -> ResultWork.Error(DataError.Network.FORBIDDEN)
        404 -> ResultWork.Error(DataError.Network.NOT_FOUND)
        408 -> ResultWork.Error(DataError.Network.REQUEST_TIMEOUT)
        429 -> ResultWork.Error(DataError.Network.TOO_MANY_REQUESTS)
        500 -> ResultWork.Error(DataError.Network.SERVER_ERROR)
        503 -> ResultWork.Error(DataError.Network.SERVICE_UNAVAILABLE)
        else -> ResultWork.Error(DataError.Network.UNKNOWN)
    }
}
