package ru.modulmoney.write_off

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme

@Composable
fun WarningDialogContent(
    modifier: Modifier = Modifier,
    confirmUpdate: () -> Unit,
    cancelUpdate: () -> Unit,
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp),
    ) {
        Text(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(R.string.warning_send_write_off),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = modifier.height(10.dp))
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                modifier = modifier.weight(1f),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent
                ),
                onClick = cancelUpdate
            ) {
                Text(
                    modifier = modifier,
                    text = stringResource(R.string.not),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(modifier = modifier.width(10.dp))
            Button(
                modifier = modifier.weight(1f),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                onClick = confirmUpdate
            ) {
                Text(
                    modifier = modifier,
                    text = stringResource(R.string.yes),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview1() {
    MobileAppAndroidForTerminalTheme {
        WarningDialogContent(
            confirmUpdate = {},
            cancelUpdate = {}
        )
    }
}