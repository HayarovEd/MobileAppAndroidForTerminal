package by.liscode.data.handler


import by.liscode.domain.utils.DataError
import com.nine.statio.domain.utils.ResultWork
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        } catch (e: JsonConvertException) {
            e.printStackTrace()
            ResultWork.Error(DataError.SerializationError.FORMAT_ERROR)
        } catch (e: Exception) {
            e.printStackTrace()
            ResultWork.Error(DataError.Network.UNKNOWN)
        }
    }
}
