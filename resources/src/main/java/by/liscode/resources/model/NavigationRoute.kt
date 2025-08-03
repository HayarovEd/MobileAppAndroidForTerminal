package by.liscode.resources.model
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import by.liscode.resources.R
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
    data object Settings : NavigationRoute()
}

@Composable
fun routes() = listOf(
    TopLevelRoute(
        stringResource(R.string.fund),
        NavigationRoute.Fund,
        ImageVector.vectorResource(R.drawable.baseline_home_24)
    ),
    TopLevelRoute(
        stringResource(R.string.orders),
        NavigationRoute.Orders,
        ImageVector.vectorResource(R.drawable.format_list_bulleted_24dp)
    ),
    TopLevelRoute(
        stringResource(R.string.settings),
        NavigationRoute.Settings,
        ImageVector.vectorResource(R.drawable.settings_24dp)
    )
)