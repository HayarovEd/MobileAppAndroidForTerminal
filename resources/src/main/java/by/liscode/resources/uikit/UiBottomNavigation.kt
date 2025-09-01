package by.liscode.resources.uikit

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import by.liscode.resources.model.TopLevelRoute
import by.liscode.resources.model.routes
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme

@Composable
fun UiBottomNavigation(
    modifier: Modifier = Modifier,
    routes: List<TopLevelRoute>,
    navController: NavHostController
) {


    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = navBackStackEntry?.destination

    Row(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        routes.forEach { destination ->
            val selectedColor = if (currentDestination?.hierarchy?.any {
                    it.hasRoute(destination.route::class)
                } == true) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline
            val selectedBackgroundColor = if (currentDestination?.hierarchy?.any {
                    it.hasRoute(destination.route::class)
                } == true) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent
            Column(
                modifier = modifier
                    .weight(1f)
                    .clip(shape = MaterialTheme.shapes.small)
                    .background(color = selectedBackgroundColor)
                    .padding(vertical = 4.dp)
                    .clickable {
                        navController.navigate(destination.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = "",
                    tint = selectedColor
                )
                Spacer(modifier = modifier.height(5.dp))
                Text(
                    modifier = modifier,
                    text = destination.name,
                    color = selectedColor,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Preview()
@Composable
private fun UiBottomNavigation1() {
    MobileAppAndroidForTerminalTheme {
        UiBottomNavigation(
            navController = rememberNavController(),
            routes = routes()
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun UiBottomNavigation2() {
    MobileAppAndroidForTerminalTheme {
        UiBottomNavigation(
            navController = rememberNavController(),
            routes = routes()
        )
    }
}