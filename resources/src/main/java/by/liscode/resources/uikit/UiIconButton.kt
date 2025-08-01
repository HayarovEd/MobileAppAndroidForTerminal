package by.liscode.resources.uikit

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun UiIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.secondary,
    buttonColor: Color = Color.Transparent,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = buttonColor
        ),
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            tint = color
        )
    }
}