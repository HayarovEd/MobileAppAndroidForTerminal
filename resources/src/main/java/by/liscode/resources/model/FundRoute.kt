package by.liscode.resources.model

import by.liscode.domain.model.UserRole
import by.liscode.domain.model.UserType
import by.liscode.resources.R

data class FundRoute (
    val nameResId: Int,
    val route: NavigationRoute,
    val accessedRoles: List<UserRole>,
    val accessedTypes: List<UserType>,
)

val fundRoutes = listOf(
    FundRoute(
        nameResId = R.string.take_work,
        route = NavigationRoute.WorkTake,
        accessedRoles = listOf(UserRole.CLIENT_EMPLOYEE),
        accessedTypes = listOf(UserType.HOTEL),
    ),
    FundRoute(
        nameResId = R.string.take_balance,
        route = NavigationRoute.BalanceTake,
        accessedRoles = listOf(UserRole.CLIENT_EMPLOYEE),
        accessedTypes = listOf(UserType.HOTEL),
    ),
    FundRoute(
        nameResId = R.string.reset_balance,
        route = NavigationRoute.BalanceDeduct,
        accessedRoles = listOf(UserRole.CLIENT_EMPLOYEE),
        accessedTypes = listOf(UserType.HOTEL),
    ),
    FundRoute(
        nameResId = R.string.inventory,
        route = NavigationRoute.Inventory,
        accessedRoles = listOf(UserRole.CLIENT_EMPLOYEE),
        accessedTypes = listOf(UserType.HOTEL),
    ),
    FundRoute(
        nameResId = R.string.search,
        route = NavigationRoute.Search,
        accessedRoles = listOf(UserRole.CLIENT_EMPLOYEE),
        accessedTypes = listOf(UserType.HOTEL),
    ),
)