package by.liscode.data.repository

import by.liscode.data.handler.handleRemoteResponse
import by.liscode.data.remote.response.CounterPartyResponse
import by.liscode.domain.model.Counterparty
import by.liscode.domain.repository.RemoteRepository
import by.liscode.domain.utils.DataError
import by.liscode.domain.utils.TERMINAL_COUNTERPARTY
import com.nine.statio.domain.utils.ResultWork
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
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
                println(response.bodyAsText())
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
}