package by.liscode.resources.model
import kotlinx.serialization.Serializable

sealed class NavigationRoute {
    @Serializable
    data object Login : NavigationRoute()

    @Serializable
    data class Registration(
        val typeUser: Int
    ) : NavigationRoute()

    @Serializable
    data object Splash : NavigationRoute()

    @Serializable
    data object DriverOrders : NavigationRoute()

    @Serializable
    data object DriverProfile : NavigationRoute()

    @Serializable
    data object DriverCabinet : NavigationRoute()

    @Serializable
    data object ClientProfile : NavigationRoute()

    @Serializable
    data object ClientCabinet : NavigationRoute()

    @Serializable
    data object ClientExcursion : NavigationRoute()


    @Serializable
    data class DriverOrder(
        val orderId: Int,
        val isDeeplink: Boolean = false
    ) : NavigationRoute()

    @Serializable
    data class ClientOrder(
        val orderId: Int,
        val isDeeplink: Boolean = false
    ) : NavigationRoute()

    @Serializable
    data class Excursion(
        val id: Int
    ) : NavigationRoute()

    @Serializable
    data class PaymentRoute(
        val amount: Double,
        val driverId: Int,
    ) : NavigationRoute()
}