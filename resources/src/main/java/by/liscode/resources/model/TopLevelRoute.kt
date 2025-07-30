package by.liscode.resources.model

import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelRoute(
    val name: String,
    val route: NavigationRoute,
    val icon: ImageVector
)