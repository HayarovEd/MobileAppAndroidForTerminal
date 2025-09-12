package by.liscode.data.repository

import by.liscode.data.handler.handleRemoteResponse
import by.liscode.data.mapper.convertToLinen
import by.liscode.data.remote.request.CounterpartyPost
import by.liscode.data.remote.response.CounterPartyResponse
import by.liscode.data.remote.response.CounterPartyResponseFromPost
import by.liscode.data.remote.response.LinenDto
import by.liscode.data.remote.response.StateLinenResponse
import by.liscode.data.remote.response.StatusLinenResponse
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Linen
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.repository.RemoteRepository
import by.liscode.domain.utils.DataError
import by.liscode.domain.utils.TERMINAL_COUNTERPARTY
import by.liscode.domain.utils.TERMINAL_ITEMS
import by.liscode.domain.utils.TERMINAL_LINEN_STATE
import by.liscode.domain.utils.TERMINAL_LINEN_STATUS
import by.liscode.domain.utils.convertToStateLinen
import by.liscode.domain.utils.convertToStatusLinen
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

    override suspend fun addCounterparty(
        baseUrl: String,
        token: String,
        name: String,
        address: String,
        inn: String,
        phone: String,
    ): ResultWork<Counterparty, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.post("$baseUrl/$TERMINAL_COUNTERPARTY") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(
                        CounterpartyPost(
                            name = name,
                            address = address,
                            inn = inn,
                            phone = phone
                        )
                    )
                }
                //Log.d("TEST IMPULS CAMERA", "response ${response.bodyAsText()}")
                val result = response.body<CounterPartyResponseFromPost>()
                Counterparty(
                    id = result.counterparty.id,
                    name = result.counterparty.name,
                    address = result.counterparty.address,
                    inn = result.counterparty.inn,
                    phone = result.counterparty.phone
                )
            }
        }
    }

    override suspend fun getLinenByRfid(
        baseUrl: String,
        token: String,
        rfid: String,
    ): ResultWork<Linen, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_ITEMS/$rfid") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val result = response.body<LinenDto>()
                result.convertToLinen()
            }
        }
    }
}