package by.liscode.data.repository

import by.liscode.data.handler.handleRemoteResponse
import by.liscode.data.mapper.convertToClient
import by.liscode.data.remote.request.BasicAuthRequest
import by.liscode.data.remote.response.AuthDto
import by.liscode.data.remote.response.ClientProfileResponse
import by.liscode.domain.model.Auth
import by.liscode.domain.model.Client
import by.liscode.domain.repository.AuthorizeRepository
import by.liscode.domain.utils.AUTH_POSTFIX
import by.liscode.domain.utils.TERMINAL_CLIENTS
import by.liscode.domain.utils.DataError
import com.nine.statio.domain.utils.ResultWork
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthorizeRepositoryImpl(
    private val httpClient: HttpClient
): AuthorizeRepository {


    override suspend fun authorization(
        login: String,
        password: String,
        baseUrl: String
    ): ResultWork<Auth, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.post("$baseUrl/$AUTH_POSTFIX") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        BasicAuthRequest(
                            login = login,
                            password = password,

                        )
                    )
                }.call
                    .body<AuthDto>()
                Auth(
                    token = response.token
                )
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
                val result = response.body<ClientProfileResponse>()
                result.clients.map {  it.convertToClient()}
            }
        }
    }
}