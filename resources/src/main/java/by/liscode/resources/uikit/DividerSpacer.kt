package by.liscode.resources.uikit

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DividerSpacer(
    modifier: Modifier = Modifier
) {
    Spacer(modifier = modifier.height(4.dp))
    HorizontalDivider(
        modifier = modifier.fillMaxWidth(),
        thickness = 1.dp
    )
    Spacer(modifier = modifier.height(8.dp))
}