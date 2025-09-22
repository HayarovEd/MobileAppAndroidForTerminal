package by.liscode.take_balance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.Counterparty
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.UiTextField

@Composable
fun TakeBalanceCounterpartyScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    queryCounterparty: String,
    isLoading: Boolean,
    counterparties: List<Counterparty>,
    onSearchCounterparty: (String) -> Unit,
    updateCurrentCounterParty: (Counterparty) -> Unit,
) {
    if (isLoading) {
        Box(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(15.dp),
        ) {
            CircularProgressIndicator(
                modifier = modifier
                    .align(alignment = Alignment.Center)
                    .size(200.dp)
            )
        }
    } else {
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(15.dp),
        ) {
            UiTextField(
                modifier = modifier,
                content = queryCounterparty,
                placeholder = stringResource(R.string.enter_counerparty),
                onClickContent = {
                    onSearchCounterparty(it)
                }
            )
            Spacer(modifier = modifier.height(10.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(counterparties) { counterparty ->
                    Text(
                        modifier = modifier
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(15.dp)
                            )
                            .clip(RoundedCornerShape(15.dp))
                            .fillMaxWidth()
                            .padding(15.dp)
                            .clickable(
                                onClick = { updateCurrentCounterParty(counterparty) }
                            ),
                        text = "${counterparty.name}, ${counterparty.address}",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview1() {
    val counterParties = (1..5).map {
        Counterparty(
            id = it,
            name = "name $it",
            address = "Russia, Moscow, Baumana, $it",
            inn = "333-333-333-0$it",
            phone = "+7(555)55-55-222"
        )
    }
    MobileAppAndroidForTerminalTheme {
        TakeBalanceCounterpartyScreen(
            counterparties = counterParties,
            queryCounterparty = "",
            isLoading = false,
            paddingValues = PaddingValues(10.dp),
            onSearchCounterparty = {},
            updateCurrentCounterParty = {}
        )
    }
}

@Preview
@Composable
private fun Preview2() {
    val counterParties = (1..5).map {
        Counterparty(
            id = it,
            name = "name $it",
            address = "Russia, Moscow, Baumana, $it",
            inn = "333-333-333-0$it",
            phone = "+7(555)55-55-222"
        )
    }
    MobileAppAndroidForTerminalTheme {
        TakeBalanceCounterpartyScreen(
            counterparties = counterParties,
            isLoading = false,
            queryCounterparty = "ddddd",
            paddingValues = PaddingValues(10.dp),
            onSearchCounterparty = {},
            updateCurrentCounterParty = {}
        )
    }
}