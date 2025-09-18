package by.liscode.resources.model
import kotlinx.serialization.Serializable

sealed class NavigationRoute {
    @Serializable
    data object Login : NavigationRoute()

    @Serializable
    data object Registration : NavigationRoute()

    @Serializable
    data object Splash : NavigationRoute()

    @Serializable
    data object Fund : NavigationRoute()

    @Serializable
    data object WorkTake : NavigationRoute()

    @Serializable
    data object BalanceTake : NavigationRoute()

    @Serializable
    data object BalanceDeduct : NavigationRoute()

    @Serializable
    data object Inventory : NavigationRoute()

    @Serializable
    data object Search : NavigationRoute()

    @Serializable
    data object Orders : NavigationRoute()

    @Serializable
    data object OrderCreate : NavigationRoute()

    @Serializable
    data class CurrentOrder(val id: String) : NavigationRoute()

    @Serializable
    data object Settings : NavigationRoute()
}