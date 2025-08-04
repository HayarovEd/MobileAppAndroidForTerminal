package by.liscode.resources.model

import by.liscode.resources.R

data class FundRoute (
    val nameResId: Int,
    val route: NavigationRoute,
)

val fundRoutes = listOf(
    FundRoute(
        nameResId = R.string.take_work,
        route = NavigationRoute.WorkTake
    ),
    FundRoute(
        nameResId = R.string.take_balance,
        route = NavigationRoute.BalanceTake
    ),
    FundRoute(
        nameResId = R.string.reset_balance,
        route = NavigationRoute.BalanceDeduct
    ),
    FundRoute(
        nameResId = R.string.inventory,
        route = NavigationRoute.Inventory
    ),
    FundRoute(
        nameResId = R.string.search,
        route = NavigationRoute.Search
    ),
)