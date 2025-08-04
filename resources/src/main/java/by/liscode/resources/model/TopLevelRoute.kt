package by.liscode.resources.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import by.liscode.resources.R

data class TopLevelRoute(
    val name: String,
    val route: NavigationRoute,
    val icon: ImageVector
)

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