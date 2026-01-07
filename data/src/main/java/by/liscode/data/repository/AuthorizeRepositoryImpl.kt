package by.liscode.data.repository

import by.liscode.data.handler.handleRemoteResponse
import by.liscode.data.mapper.convertToClient
import by.liscode.data.remote.request.BasicAuthRequest
import by.liscode.data.remote.response.AuthDto
import by.liscode.data.remote.response.ClientsProfileResponse
import by.liscode.domain.model.Auth
import by.liscode.domain.model.Client
import by.liscode.domain.repository.AuthorizeRepository
import by.liscode.domain.utils.AUTH_POSTFIX
import by.liscode.domain.utils.TERMINAL_CLIENTS
import by.liscode.domain.utils.DataError
import com.nine.statio.domain.utils.ResultWork
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.JsonConvertException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

class AuthorizeRepositoryImpl(
    private val httpClient: HttpClient
) : AuthorizeRepository {


    override suspend fun authorization(
        login: String,
        password: String,
        baseUrl: String
    ): ResultWork<Auth, DataError> {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.post("$baseUrl/$AUTH_POSTFIX") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        BasicAuthRequest(
                            login = login,
                            password = password,

                            )
                    )
                }
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val body = response.call
                            .body<AuthDto>()
                        ResultWork.Success(
                            Auth(
                                token = body.token
                            )
                        )
                    }

                    HttpStatusCode.Unauthorized -> {
                        ResultWork.Error(DataError.Network.UNAUTHORIZED)
                    }

                    else -> {
                        ResultWork.Error(DataError.Network.UNKNOWN)
                    }
                }
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
            } catch (e: JsonConvertException) {
                e.printStackTrace()
                ResultWork.Error(DataError.SerializationError.FORMAT_ERROR)
            } catch (e: Exception) {
                e.printStackTrace()
                ResultWork.Error(DataError.Network.UNKNOWN)
            }
        }
    }

    override suspend fun getProfile(
        baseUrl: String,
        token: String,
    ): ResultWork<List<Client>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_CLIENTS") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val result = response.body<ClientsProfileResponse>()
                result.clients.map { it.convertToClient() }
            }
        }
    }
}