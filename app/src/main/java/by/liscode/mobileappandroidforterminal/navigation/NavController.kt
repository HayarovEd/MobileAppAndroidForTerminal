package by.liscode.mobileappandroidforterminal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import by.liscode.resources.model.NavigationRoute
import by.liscode.resources.model.routes
import by.liscode.resources.uikit.UiBottomNavigation
import by.liscode.settings.SettingsScreenRoot

@Composable
fun NavController(
    startDestination: NavigationRoute = NavigationRoute.Login,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable<NavigationRoute.Login> {

        }
        composable<NavigationRoute.Registration> {

        }
        composable<NavigationRoute.Fund> {
            /*AdminProfileScreenRoot(
                bottomRoutes = {
                    UiBottomNavigation(
                        routes = adminRoutes,
                        navController = navController
                    )
                },
                onClickLogOut = {
                    navController.navigate(NavigationRoute.Login)
                }
            )*/
        }

        composable<NavigationRoute.Orders> {

        }

        composable<NavigationRoute.Settings> {
            SettingsScreenRoot (
                bottomRoutes = {
                    UiBottomNavigation(
                        routes = routes(),
                        navController = navController
                    )
                },
                onNavigateToLogin = {
                    navController.navigate(NavigationRoute.Login)
                },
            )
        }

        composable<NavigationRoute.Splash> {

        }

        composable<NavigationRoute.BalanceDeduct> {

        }

        composable<NavigationRoute.BalanceTake> {

        }

        composable<NavigationRoute.Inventory> {

        }

        composable<NavigationRoute.WorkTake> {

        }

        composable<NavigationRoute.Search> {

        }

    }
}