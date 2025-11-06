package by.liscode.data.repository

import android.util.Log
import by.liscode.data.handler.handleRemoteResponse
import by.liscode.data.mapper.convertToClient
import by.liscode.data.mapper.convertToLinen
import by.liscode.data.mapper.convertToOrder
import by.liscode.data.mapper.convertToTypeLinen
import by.liscode.data.mapper.convertToinventory
import by.liscode.data.remote.request.CounterpartyPost
import by.liscode.data.remote.request.CreateOrderRequest
import by.liscode.data.remote.request.LinenInToStockRequest
import by.liscode.data.remote.request.LinenWtiteOffRequest
import by.liscode.data.remote.request.RfidInToUseRequest
import by.liscode.data.remote.request.UpdateOrderRequest
import by.liscode.data.remote.response.ClientItemsResponse
import by.liscode.data.remote.response.ClientProfileResponse
import by.liscode.data.remote.response.ClientsProfileResponse
import by.liscode.data.remote.response.CounterPartyResponse
import by.liscode.data.remote.response.CounterPartyResponseFromPost
import by.liscode.data.remote.response.InventoryResponse
import by.liscode.data.remote.response.LinenDtoMultiResponse
import by.liscode.data.remote.response.LinenDtoSingleResponse
import by.liscode.data.remote.response.OrderDtoResponse
import by.liscode.data.remote.response.OrdersResponse
import by.liscode.data.remote.response.SingleOrderResponse
import by.liscode.data.remote.response.TypesLinenResponse
import by.liscode.domain.model.Client
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Inventory
import by.liscode.domain.model.Linen
import by.liscode.domain.model.Order
import by.liscode.domain.model.ReasonWriteOff
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.StatusOrder
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.repository.RemoteRepository
import by.liscode.domain.utils.ACCEPT_INTO_STOCK
import by.liscode.domain.utils.ACCEPT_IN_PRODUCTION
import by.liscode.domain.utils.DataError
import by.liscode.domain.utils.FORM_AFTER_PRODUCTION
import by.liscode.domain.utils.TERMINAL_CLIENTS
import by.liscode.domain.utils.TERMINAL_COUNTERPARTY
import by.liscode.domain.utils.TERMINAL_INVENTORY
import by.liscode.domain.utils.TERMINAL_ITEMS
import by.liscode.domain.utils.TERMINAL_ITEMS_BY_RFID
import by.liscode.domain.utils.TERMINAL_ITEMS_INTO_USE
import by.liscode.domain.utils.TERMINAL_ITEMS_IN_STOCK
import by.liscode.domain.utils.TERMINAL_ITEMS_WRITE_OFF
import by.liscode.domain.utils.TERMINAL_LINEN_STATE
import by.liscode.domain.utils.TERMINAL_LINEN_STATUS
import by.liscode.domain.utils.TERMINAL_LINEN_TIPES
import by.liscode.domain.utils.TERMINAL_ORDERS
import by.liscode.domain.utils.TERMINAL_ORDER_STATUSES
import by.liscode.domain.utils.TERMINAL_WRITE_OFF_REASONS
import by.liscode.domain.utils.convertToReasonWriteOff
import by.liscode.domain.utils.convertToReasonWriteOffString
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
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

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
                val result = parseManually(
                    response = response,
                    keyResponse = "item_states"
                )
                result.map {
                    convertToStateLinen(it.key)
                }
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
                val result = parseManually(
                    response = response,
                    keyResponse = "item_statuses"
                )
                result.map {
                    convertToStatusLinen(it.key)
                }
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
                val result = parseManually(
                    response = response,
                    keyResponse = "order_statuses"
                )
                result.map {
                    convertToStatusOrder(it.key)
                }
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
                val result = parseManually(
                    response = response,
                    keyResponse = "write_off_reasons"
                )
                result.map {
                    convertToReasonWriteOff(it.key)
                }
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

    override suspend fun acceptToLinensInToStock(
        baseUrl: String,
        token: String,
        rfids: List<String>,
        typeId: Int,
        nomenclature: String,
        weight: Int,
        consignment: String,
        counterpartyId: Int,
    ): ResultWork<List<Linen>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.post("$baseUrl/$TERMINAL_ITEMS_IN_STOCK") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(
                        LinenInToStockRequest(
                            consignment = consignment,
                            counterpartyId = counterpartyId,
                            nomenclature = nomenclature,
                            rfids = rfids,
                            typeId = typeId,
                            weight = weight
                        )
                    )
                }
                //Log.d("TEST IMPULS CAMERA", "response ${response.bodyAsText()}")
                val result = response.body<ClientItemsResponse>()
                result.linenDto.map { it.convertToLinen() }
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
                val response = httpClient.get("$baseUrl/$TERMINAL_ITEMS_BY_RFID/$rfid") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                Log.d("TEST RFID TERMINAL", "by rfid - ${response.bodyAsText()}")
                val result = response.body<LinenDtoSingleResponse>()
                result.item.convertToLinen()
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

    override suspend fun writeOffLinens(
        baseUrl: String,
        token: String,
        rfids: List<String>,
        reason: ReasonWriteOff,
        comment: String,
    ): ResultWork<List<Linen>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.post("$baseUrl/$TERMINAL_ITEMS_WRITE_OFF") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(
                        LinenWtiteOffRequest(
                            comment = comment,
                            reason = convertToReasonWriteOffString(reason),
                            rfids = rfids
                        )
                    )
                }
                val result = response.body<LinenDtoMultiResponse>()
                result.items.map { it.convertToLinen() }
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

    override suspend fun sendLinenInToUse(
        baseUrl: String,
        token: String,
        rfids: List<String>
    ): ResultWork<List<Linen>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.post("$baseUrl/$TERMINAL_ITEMS_INTO_USE") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(
                        RfidInToUseRequest(
                            rfids = rfids
                        )
                    )
                }
                val result = response.body<LinenDtoMultiResponse>()
                result.items.map { it.convertToLinen() }
            }
        }
    }

    override suspend fun getOrders(
        baseUrl: String,
        token: String
    ): ResultWork<List<Order>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_ORDERS") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val textRes = response.bodyAsText()
                Log.d("TEST TERMINAL DATE", "response $response")
                val result = response.body<OrdersResponse>().orderDtos
                result.map { it.convertToOrder() }
            }
        }
    }

    override suspend fun getOrderById(
        baseUrl: String,
        token: String,
        id: Int
    ): ResultWork<Order, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.get("$baseUrl/$TERMINAL_ORDERS/$id") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                }
                val result = response.body<SingleOrderResponse>().orderDtoShort
                result.convertToOrder()
            }
        }
    }

    override suspend fun createOrder(
        baseUrl: String,
        token: String,
        rfids: List<String>,
        laundryId: Int,
        address: String,
        sendAct: Boolean,
    ): ResultWork<Order, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.post("$baseUrl/$TERMINAL_ORDERS") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(
                        CreateOrderRequest(
                            address = address,
                            laundryId = laundryId,
                            rfids = rfids,
                            sendAct = sendAct
                        )
                    )
                }
                val result = response.body<OrderDtoResponse>()
                result.orderDto.convertToOrder()
            }
        }
    }

    override suspend fun acceptOrderInUse(
        baseUrl: String,
        token: String,
        orderId: Int,
        ignoreMissing: Boolean,
        rfids: List<String>,
    ): ResultWork<Order, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.post("$baseUrl/$TERMINAL_ORDERS/$orderId/$ACCEPT_IN_PRODUCTION") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(
                        UpdateOrderRequest(
                            rfids = rfids,
                            ignoreMissing = ignoreMissing
                        )
                    )
                }

                val result = response.body<SingleOrderResponse>()
                result.orderDtoShort.convertToOrder()
            }
        }
    }

    override suspend fun formOrderAfterProduction(
        baseUrl: String,
        token: String,
        orderId: Int,
        ignoreMissing: Boolean,
        rfids: List<String>,
    ): ResultWork<Order, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.post("$baseUrl/$TERMINAL_ORDERS/$orderId/$FORM_AFTER_PRODUCTION") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(
                        UpdateOrderRequest(
                            rfids = rfids,
                            ignoreMissing = ignoreMissing
                        )
                    )
                }
                Log.d("REST TERMINAL", response.bodyAsText())
                val result = response.body<SingleOrderResponse>()
                result.orderDtoShort.convertToOrder()
            }
        }
    }

    override suspend fun acceptOrderInToStock(
        baseUrl: String,
        token: String,
        orderId: Int,
        ignoreMissing: Boolean,
        rfids: List<String>,
    ): ResultWork<Order, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.post("$baseUrl/$TERMINAL_ORDERS/$orderId/$ACCEPT_INTO_STOCK") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(
                        UpdateOrderRequest(
                            rfids = rfids,
                            ignoreMissing = ignoreMissing
                        )
                    )
                }
                val result = response.body<SingleOrderResponse>()
                result.orderDtoShort.convertToOrder()
            }
        }
    }


    override suspend fun sendResultInventory(
        baseUrl: String,
        token: String,
        rfids: List<String>,
    ): ResultWork<List<Inventory>, DataError> {
        return withContext(Dispatchers.IO) {
            handleRemoteResponse {
                val response = httpClient.post("$baseUrl/$TERMINAL_INVENTORY") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(
                        RfidInToUseRequest(
                            rfids = rfids,
                        )
                    )
                }
                val result = response.body<InventoryResponse>()
                result.inventoryDto.map { it.convertToinventory() }
            }
        }
    }

    private suspend fun parseManually(
        response: HttpResponse,
        keyResponse: String
    ): Map<String, String> {
        val jsonObject = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        val writeOffReasonsObject = jsonObject[keyResponse]!!.jsonObject
        return writeOffReasonsObject.mapValues { it.value.toString().removeSurrounding("\"") }
    }
}