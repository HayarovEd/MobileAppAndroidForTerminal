package by.liscode.resources.uikit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Linen
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.utils.printFormat
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme

@Composable
fun ItemLinen(
    modifier: Modifier = Modifier,
    colorType: Color = MaterialTheme.colorScheme.onBackground,
    linen: Linen,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = modifier.weight(2f),
        ) {
            Text(
                modifier = modifier,
                text = linen.printFormat(),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = modifier.height(10.dp))
            Text(
                modifier = modifier,
                text = "${stringResource(R.string.rfid)}: ${linen.rfid}",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(modifier = modifier.width(15.dp))
        Column(
            modifier = modifier.weight(1f),
        ) {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = linen.status.asStatusLinenText().asString(),
                color = colorType,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ItemLinenView() {
    MobileAppAndroidForTerminalTheme {
        ItemLinen(
            linen = Linen(
                id = 1,
                clientId = 0,
                rfid = "300ed300ed300ed300ed",
                typeId = 1,
                typeLinen = TypeLinen(
                    id = 1,
                    name = "Пододеяльник",
                    size = "200x220",
                    fabric = "сатин",
                    composition = "3x3",
                    color = "белый",
                    density = "100%",
                    sku = "хлопок",
                    maxWash = 10,
                    clientId = 1
                ),
                discount = "dsk",
                weight = 135,
                state = StateLinen.NEW,
                washCount = 3,
                status = StatusLinen.IN_STOCK,
                price = "100 р.",
                counterpartyId = 3,
                counterparty = Counterparty(
                    id = 3,
                    name = "ООО Вектор",
                    address = "Москва",
                    inn = "333-333-333-333",
                    phone = "+79123456789"
                )
            )
        )
    }
}