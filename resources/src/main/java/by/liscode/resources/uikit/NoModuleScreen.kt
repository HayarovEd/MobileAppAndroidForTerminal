package by.liscode.resources.uikit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.resources.R

@Preview
@Composable
fun NoModuleScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = modifier,
            text = stringResource(R.string.no_module),
            color = colorScheme.error,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}