package by.liscode.domain.model

enum class StatusOrder(val number: Int) {
    REGISTERED(0),
    IN_TRANSIT(1),
    IN_PRODUCTION(2),
    FORMED(3),
    IN_DELIVERY(4),
    COMPLETED(5)
}