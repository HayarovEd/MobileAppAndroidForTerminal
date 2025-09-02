package by.liscode.domain.model


//Клиент
data class Client(
    val id: Int,
    val createdAt: String,
    val email: String,
    val name: String,
    val location: String,
    val phone: String
)

//Исполнитель
data class Worker(
    val id: Int,
    val createdAt: String,
    val email: String,
    val name: String,
    val location: String,
    val phone: String
)

//тип пользователя, что-нибудь еще?
enum class Role {
    MANAGER,
    STOCK,
}


//Пользователь
data class User(
    val id: Int,
    val login: String,
    val password: String,
    val role: Role,
    val clientId: Int?
)


//терминал
data class Terminal(
    val id: Int,
    val createdAt: String,
    val clientId: Int?
)

//статус белья, что-нибудь еще?
enum class LaundryWorkStatus {
    IN_STOCK,
    IN_WORK
}

//Тип белья
data class LaundryType(
    val id: Int,
    val type: String,
)

//состояние белья, что-нибудь еще?
enum class LaundryStatus {
    NEW,
}

//контрагент
/*data class Counterparty(
    val id: Int,
    val createdAt: String,
    val name: String,
    val address: String,
    val inn: String,
    val phone: String,
)*/

//Белье
data class Laundry(
    val id: Int,
    val marker: String,
    val laundryWorkStatus: LaundryWorkStatus,
    val laundryType: LaundryType,
    val laundryStatus: LaundryStatus,
    val counterparty: Counterparty,
    val articleNumber: String,
    val weight: Double,
    // val price: Double,
    val invoice: String,
)

//причина списания, что-нибудь еще?
enum class ReasonWriteOff {
    DAMAGED,
}


data class WriteOff(
    val id: Int,
    val reasonWriteOff: ReasonWriteOff,
    val act: String,
    val items: List<Laundry>
)


//статус заказа, что-нибудь еще?
enum class OrderStatus {
    ADOPTED,
}

//Заказ
data class Order(
    val id: Int,
    val createdAt: String,
    val orderStatus: OrderStatus,
    val clientId: Int,
    val worker: Worker
)


