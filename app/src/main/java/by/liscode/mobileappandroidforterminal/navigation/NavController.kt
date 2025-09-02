package by.liscode.mobileappandroidforterminal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import by.liscode.fund.FundScreenRoot
import by.liscode.login.LoginScreenRoot
import by.liscode.regisration.RegistrationScreenRoot
import by.liscode.resources.model.NavigationRoute
import by.liscode.resources.model.routes
import by.liscode.resources.uikit.UiBottomNavigation
import by.liscode.settings.SettingsScreenRoot
import by.liscode.splash.SplashScreenRoot
import com.edurda77.take_work.TakeWorkScreenRoot

@Composable
fun NavController(
    startDestination: NavigationRoute = NavigationRoute.Splash,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable<NavigationRoute.Login> {
            LoginScreenRoot(
                onNavigateToFundScreen = {
                    navController.navigate(NavigationRoute.Fund)
                },
                onNavigateToRegisterScreen = {
                    navController.navigate(NavigationRoute.Registration)
                }
            )
        }
        composable<NavigationRoute.Registration> {
            RegistrationScreenRoot(
                onNavigateToLogin = {
                    navController.navigateUp()
                }
            )
        }
        composable<NavigationRoute.Fund> {
            FundScreenRoot (
                bottomRoutes = {
                    UiBottomNavigation(
                        routes = routes(),
                        navController = navController
                    )
                },
               onNavigateTo = {
                   navController.navigate(it)
               }
            )
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
            SplashScreenRoot(
                onNavigateToFundScreen = {
                    navController.navigate(NavigationRoute.Fund)
                },
                onNavigateToLoginScreen = {
                    navController.navigate(NavigationRoute.Login)
                }
            )
        }

        composable<NavigationRoute.BalanceDeduct> {

        }

        composable<NavigationRoute.BalanceTake> {

        }

        composable<NavigationRoute.Inventory> {

        }

        composable<NavigationRoute.WorkTake> {
            TakeWorkScreenRoot(
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<NavigationRoute.Search> {

        }

    }
}