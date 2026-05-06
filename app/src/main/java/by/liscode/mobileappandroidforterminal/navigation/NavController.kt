package by.liscode.mobileappandroidforterminal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import by.liscode.fund.FundScreenRoot
import by.liscode.login.LoginScreenRoot
import by.liscode.orders.OrdersScreenRoot
import by.liscode.regisration.RegistrationScreenRoot
import by.liscode.resources.model.NavigationRoute
import by.liscode.resources.model.routes
import by.liscode.resources.uikit.UiBottomNavigation
import by.liscode.settings.SettingsScreenRoot
import by.liscode.splash.SplashScreenRoot
import by.liscode.search.SearchScreenRoot
import by.liscode.take_balance.TakeBalanceScreenRoot
import com.edurda77.take_work.TakeWorkScreenRoot
import by.liscode.inventory.CurrentOrderScreenRoot
import by.liscode.inventory.InventoryScreenRoot
import by.liscode.order_create.OrderCreateScreenRoot
import by.liscode.write_off.WriteOffScreenRoot

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
            FundScreenRoot(
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
            OrdersScreenRoot(
                bottomRoutes = {
                    UiBottomNavigation(
                        routes = routes(),
                        navController = navController
                    )
                },
                onNavigateToOrderScreen = {
                    navController.navigate(NavigationRoute.CurrentOrder(it))
                },
                onNavigateToCreateOrderScreen = {
                    navController.navigate(NavigationRoute.OrderCreate)
                },
                onNavigateToLoginScreen = {
                    navController.navigate(NavigationRoute.Login)
                }
            )
        }

        composable<NavigationRoute.OrderCreate> {
            OrderCreateScreenRoot(
                onNavigateToLoginScreen = {
                    navController.navigate(NavigationRoute.Login)
                },
                onBackNavigate = {
                    navController.navigateUp()
                }
            )
        }

        composable<NavigationRoute.CurrentOrder> {
            CurrentOrderScreenRoot(
                onNavigateToLoginScreen = {
                    navController.navigate(NavigationRoute.Login)
                },
                onBackNavigate = {
                    navController.navigateUp()
                }
            )
        }

        composable<NavigationRoute.Settings> {
            SettingsScreenRoot(
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
            WriteOffScreenRoot(
                onNavigateToLoginScreen = {
                    navController.navigate(NavigationRoute.Login)
                },
                onBackNavigate = {
                    navController.navigateUp()
                }
            )
        }

        composable<NavigationRoute.BalanceTake> {
            TakeBalanceScreenRoot(
                onNavigateToLoginScreen = {
                    navController.navigate(NavigationRoute.Login)
                },
                onBackNavigate = {
                    navController.navigateUp()
                }
            )
        }

        composable<NavigationRoute.Inventory> {
            InventoryScreenRoot(
                onNavigateToLoginScreen = {
                    navController.navigate(NavigationRoute.Login)
                },
                onBackNavigate = {
                    navController.navigateUp()
                }
            )
        }

        composable<NavigationRoute.WorkTake> {
            TakeWorkScreenRoot(
                onNavigateToLoginScreen = {
                    navController.navigate(NavigationRoute.Login)
                },
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<NavigationRoute.Search> {
            SearchScreenRoot(
                onNavigateToLoginScreen = {
                    navController.navigate(NavigationRoute.Login)
                },
                onBackNavigate = {
                    navController.navigateUp()
                }
            )
        }

    }
}