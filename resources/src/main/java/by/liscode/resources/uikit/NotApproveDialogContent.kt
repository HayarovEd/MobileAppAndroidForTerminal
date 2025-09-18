package by.liscode.resources.uikit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Linen
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.resources.R

@Composable
fun NotApproveDialogContent(
    modifier: Modifier = Modifier,
    notApprovedLinens: List<Linen>,
    unknownMarkers: List<String>,
    onClose: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(10.dp),
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(notApprovedLinens) {linen->
                ItemLinen(
                    linen = linen,
                    colorType = colorScheme.error
                )
            }
            item {
                HorizontalDivider(
                    modifier = modifier.fillMaxWidth(),
                    thickness = 1.dp
                )
            }
            items(unknownMarkers){ marker->
                Text(
                    modifier = modifier,
                    text = "${stringResource(R.string.unknown_marker)}: $marker",
                    color = colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        OutlinedButton(
            modifier = modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.outlinedButtonColors(),
            border = BorderStroke(
                width = 2.dp, color = colorScheme.primary
            ),
            onClick = onClose
        ) {
            Text(
                modifier = modifier,
                text = stringResource(R.string.close),
                color = colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}


@Preview
@Composable
private fun Preview() {
    val typeLinen1 = TypeLinen(
        id = 1,
        name = "Пододеяльник",
        size = "200x220",
        fabric = "сатин страйп 3х3",
        composition = "",
        color = "белый",
        density = "",
        sku = "100% хлопок",
        maxWash = 10,
        clientId = 1
    )
    val typeLinen2 = TypeLinen(
        id = 2,
        name = "Полотенце",
        size = "50x80",
        fabric = "сатин страйп 3х3",
        composition = "",
        color = "белый",
        density = "",
        sku = "100% хлопок",
        maxWash = 10,
        clientId = 1
    )
    val markers = (1..4).map { "Marker N$it" }
    val linens = (1..14).map {
        Linen(
            id = it,
            rfid = "Marker N$it",
            typeId = it + 3,
            typeLinen = if (it % 2 == 0) typeLinen1 else typeLinen2,
            discount = "",
            weight = 135,
            state = StateLinen.entries.random(),
            washCount = 9,
            status = StatusLinen.IN_USE,
            price = "",
            counterpartyId = 1,
            counterparty = Counterparty(
                id = it,
                name = "name $it",
                address = "Russia, Moscow, Baumana, $it",
                inn = "333-333-333-0$it",
                phone = "+7(555)55-55-222"
            )
        )
    }
    NotApproveDialogContent(
        notApprovedLinens = linens,
        unknownMarkers = markers,
        onClose = {}
    )
}