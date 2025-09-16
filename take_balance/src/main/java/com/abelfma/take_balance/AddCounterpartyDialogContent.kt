package com.abelfma.take_balance

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.UiTextField
import by.liscode.resources.R

@Composable
fun AddCounterpartyDialogContent(
    modifier: Modifier = Modifier,
    confirmUpdate: (String, String, String, String) -> Unit,
    cancelUpdate: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var inn by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp),
    ) {
        UiTextField(
            content = name,
            label = stringResource(R.string.enter_name_counterparty),
            onClickContent = {
                name = it
            }
        )
        Spacer(modifier = modifier.height(10.dp))
        UiTextField(
            content = address,
            label = stringResource(R.string.enter_address_counterparty),
            onClickContent = {
                address = it
            }
        )
        Spacer(modifier = modifier.height(10.dp))
        UiTextField(
            content = inn,
            label = stringResource(R.string.enter_inn_counterparty),
            onClickContent = {
                inn = it
            }
        )
        Spacer(modifier = modifier.height(10.dp))
        UiTextField(
            content = phone,
            label = stringResource(R.string.enter_phone_counterparty),
            onClickContent = {
                phone = it
            }
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
                    text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(modifier = modifier.width(10.dp))
            Button(
                modifier = modifier.weight(1f),
                shape = RoundedCornerShape(5.dp),
                enabled = name.isNotBlank()
                        && address.isNotBlank()
                        && inn.isNotBlank()
                        && phone.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    confirmUpdate(
                        name, address, inn, phone
                    )
                }
            ) {
                Text(
                    modifier = modifier,
                    text = stringResource(R.string.ok),
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
        AddCounterpartyDialogContent(
            confirmUpdate = { _, _, _, _ -> },
            cancelUpdate = {}
        )
    }
}