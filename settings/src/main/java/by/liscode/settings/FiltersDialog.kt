package by.liscode.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.Filter
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.UiTextField


@Composable
fun FiltersDialog(
    modifier: Modifier = Modifier,
    filters: List<Filter>,
    value: String,
    label: String,
    onChangeValue: (String) -> Unit,
    onConfirm: () -> Unit,
    onClear: () -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(15.dp)
    )  {
        Text(
            modifier = modifier,
            text = "${stringResource(id = R.string.add_new_filter) }:",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = modifier.height(5.dp))
        UiTextField(
            content = value,
            onClickContent = onChangeValue,
            label = label
        )
        Spacer(modifier = modifier.height(10.dp))
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.add),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = modifier.width(10.dp))
            OutlinedButton (
                modifier = modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                onClick = onClear,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = stringResource(id = R.string.clear),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Spacer(modifier = modifier.height(10.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filters) { filter->
                Text(
                    text = filter.query,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Spacer(modifier = modifier.height(10.dp))
        OutlinedButton(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
            onClick = onClose
        ) {
            Text(
                modifier = modifier,
                text = stringResource(id = R.string.close),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FiltersDialogView() {
    val filters = (1..5).map {
        Filter(
            id = it,
            query = "$it query"
        )
    }
    MobileAppAndroidForTerminalTheme{
        FiltersDialog(
            value = "Hallow",
            label = "World",
            filters = filters,
            onChangeValue = {},
            onClear = {},
            onClose = {},
            onConfirm = {}
        )
    }
}