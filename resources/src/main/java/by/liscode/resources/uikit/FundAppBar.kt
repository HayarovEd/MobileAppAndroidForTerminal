package by.liscode.resources.uikit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundAppBar(
    modifier: Modifier = Modifier,
    title: String,
    onBack: () -> Unit,
    endContent: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UiIconButton(
            modifier = modifier,
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            color = MaterialTheme.colorScheme.onPrimary,
            onClick = onBack
        )
        Text(
            modifier = modifier,
            text = title,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = modifier.weight(1f))
        Row(
            modifier = modifier,
        ){
            endContent()
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun FundAppBarView() {

    MobileAppAndroidForTerminalTheme {
        FundAppBar(
            title = "Взять в работу",
            onBack = {},
            endContent = {
                UiIconButton(
                    modifier = Modifier,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    color = MaterialTheme.colorScheme.onPrimary,
                    onClick = {}
                )
            }
        )
    }
}