package by.liscode.data.repository

import by.liscode.data.handler.handleRemoteResponse
import by.liscode.data.remote.response.CounterPartyResponse
import by.liscode.data.remote.response.StateLinenResponse
import by.liscode.data.remote.response.StatusLinenResponse
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.repository.RemoteRepository
import by.liscode.domain.utils.DataError
import by.liscode.domain.utils.TERMINAL_COUNTERPARTY
import by.liscode.domain.utils.TERMINAL_LINEN_STATE
import by.liscode.domain.utils.TERMINAL_LINEN_STATUS
import by.liscode.domain.utils.convertToStateLinen
import by.liscode.domain.utils.convertToStatusLinen
import com.nine.statio.domain.utils.ResultWork
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteRepositoryImpl(
    private val httpClient: HttpClient
): RemoteRepository {

    override suspend fun getCounterparties(
        baseUrl: String,
        token: String,
    ): ResultWork<List<Counterparty>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_COUNTERPARTY") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                response.body<CounterPartyResponse>().counterparties.map {
                    Counterparty(
                        id = it.id,
                        name = it.name,
                        address = it.address,
                        inn = it.inn,
                        phone = it.phone
                    )
                }
            }
        }
    }

    override suspend fun getStatesLinen(
        baseUrl: String,
        token: String,
    ): ResultWork<List<StateLinen>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_LINEN_STATE") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val result = response.body<StateLinenResponse>().itemStateLinenDto
                listOf(
                    convertToStateLinen(result.excellent),
                    convertToStateLinen(result.good),
                    convertToStateLinen(result.new),
                    convertToStateLinen(result.satisfactory),
                    convertToStateLinen(result.writtenOff)
                )
            }
        }
    }

    override suspend fun getStatusLinen(
        baseUrl: String,
        token: String,
    ): ResultWork<List<StatusLinen>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_LINEN_STATUS") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val result = response.body<StatusLinenResponse>().itemStatusesLinenDto
                listOf(
                    convertToStatusLinen(result.inStock),
                    convertToStatusLinen(result.inUse),
                    convertToStatusLinen(result.inOrder),
                    convertToStatusLinen(result.inTransit),
                    convertToStatusLinen(result.inProduction),
                    convertToStatusLinen(result.writtenOff),
                )
            }
        }
    }
}