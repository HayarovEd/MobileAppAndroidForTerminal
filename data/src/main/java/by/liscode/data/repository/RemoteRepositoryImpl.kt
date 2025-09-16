package by.liscode.data.repository

import by.liscode.data.handler.handleRemoteResponse
import by.liscode.data.mapper.convertToClient
import by.liscode.data.mapper.convertToEmployee
import by.liscode.data.mapper.convertToLinen
import by.liscode.data.mapper.convertToTypeLinen
import by.liscode.data.remote.request.CounterpartyPost
import by.liscode.data.remote.response.ClientItemsResponse
import by.liscode.data.remote.response.ClientProfileResponse
import by.liscode.data.remote.response.ClientsProfileResponse
import by.liscode.data.remote.response.CounterPartyResponse
import by.liscode.data.remote.response.CounterPartyResponseFromPost
import by.liscode.data.remote.response.EmployeeResponse
import by.liscode.data.remote.response.EmployeesResponse
import by.liscode.data.remote.response.LinenDto
import by.liscode.data.remote.response.ReasonWriteOffResponse
import by.liscode.data.remote.response.StateLinenResponse
import by.liscode.data.remote.response.StatusLinenResponse
import by.liscode.data.remote.response.StatusOrderResponse
import by.liscode.data.remote.response.TypesLinenResponse
import by.liscode.domain.model.Client
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Employee
import by.liscode.domain.model.Linen
import by.liscode.domain.model.ReasonWriteOff
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.StatusOrder
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.repository.RemoteRepository
import by.liscode.domain.utils.DataError
import by.liscode.domain.utils.TERMINAL_CLIENTS
import by.liscode.domain.utils.TERMINAL_COUNTERPARTY
import by.liscode.domain.utils.TERMINAL_EMPLOYEES
import by.liscode.domain.utils.TERMINAL_ITEMS
import by.liscode.domain.utils.TERMINAL_LINEN_STATE
import by.liscode.domain.utils.TERMINAL_LINEN_STATUS
import by.liscode.domain.utils.TERMINAL_LINEN_TIPES
import by.liscode.domain.utils.TERMINAL_ORDER_STATUSES
import by.liscode.domain.utils.TERMINAL_WRITE_OFF_REASONS
import by.liscode.domain.utils.convertToReasonWriteOff
import by.liscode.domain.utils.convertToStateLinen
import by.liscode.domain.utils.convertToStatusLinen
import by.liscode.domain.utils.convertToStatusOrder
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
) : RemoteRepository {

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

    override suspend fun getOrderStatuses(
        baseUrl: String,
        token: String,
    ): ResultWork<List<StatusOrder>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_ORDER_STATUSES") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val result = response.body<StatusOrderResponse>().orderStatusesDto
                listOf(
                    convertToStatusOrder(result.registered),
                    convertToStatusOrder(result.inTransit),
                    convertToStatusOrder(result.inProduction),
                    convertToStatusOrder(result.formed),
                    convertToStatusOrder(result.completed),
                )
            }
        }
    }

    override suspend fun getReasonsWriteOff(
        baseUrl: String,
        token: String,
    ): ResultWork<List<ReasonWriteOff>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_WRITE_OFF_REASONS") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val result = response.body<ReasonWriteOffResponse>().writeOffReasonsDto
                listOf(
                    convertToReasonWriteOff(result.damaged),
                    convertToReasonWriteOff(result.wornOut),
                )
            }
        }
    }

    override suspend fun getTypesLinen(
        baseUrl: String,
        token: String,
    ): ResultWork<List<TypeLinen>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_LINEN_TIPES") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val result = response.body<TypesLinenResponse>()
                result.types.map { it.convertToTypeLinen() }
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

    override suspend fun getLinens(
        baseUrl: String,
        token: String,
    ): ResultWork<List<Linen>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_ITEMS") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val result = response.body<ClientItemsResponse>()
                result.linenDto.map { it.convertToLinen() }
            }
        }
    }


    override suspend fun getEmployees(
        baseUrl: String,
        token: String,
    ): ResultWork<List<Employee>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_EMPLOYEES") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val result = response.body<EmployeesResponse>()
                result.employees.map { it.convertToEmployee() }
            }
        }
    }

    override suspend fun getEmployeeById(
        baseUrl: String,
        token: String,
        id: Int,
    ): ResultWork<Employee, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_EMPLOYEES/$id") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val result = response.body<EmployeeResponse>()
                result.employee.convertToEmployee()
            }
        }
    }

    override suspend fun getClients(
        baseUrl: String,
        token: String,
    ): ResultWork<List<Client>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_CLIENTS") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val result = response.body<ClientsProfileResponse>().clients
                result.map { it.convertToClient() }
            }
        }
    }

    override suspend fun getClientById(
        baseUrl: String,
        token: String,
        id: Int,
    ): ResultWork<Client, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_CLIENTS/$id") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val result = response.body<ClientProfileResponse>().client
                result.convertToClient()
            }
        }
    }
}