package by.liscode.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.Linen
import by.liscode.domain.model.TypeLinen
import by.liscode.resources.R
import by.liscode.resources.mock.linens
import by.liscode.resources.mock.linens3
import by.liscode.resources.mock.markers
import by.liscode.resources.mock.typeLinen2
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.UiRowThreeElements
import by.liscode.resources.uikit.UiRowThreeText
import by.liscode.resources.uikit.UiRowTitleThreeElements

@Composable
fun TypeLinenScreen(
    modifier: Modifier = Modifier,
    size: Int,
    remoteMarkers: List<String>,
    sortedGroups: Map<TypeLinen, List<Linen>>,
    ) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        UiRowThreeText(
            title1 = stringResource(R.string.type_object),
            title2 = stringResource(R.string.need),
            title3 = stringResource(R.string.read),
        )
        Spacer(modifier = modifier.height(10.dp))
        UiRowTitleThreeElements(
            title = stringResource(R.string.fund),
            firstNumber =  size,
            secondNumber = remoteMarkers.size,
        )
        Spacer(modifier = modifier.height(10.dp))
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(sortedGroups.keys.toList()) { group->
                val linensByType = sortedGroups[group]
                val status = linensByType?.size == linensByType?.count { remoteMarkers.contains(it.rfid) }
                if (!linensByType.isNullOrEmpty()) {
                    val fontColor = if (status) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    UiRowThreeElements(
                        title = group,
                        firstNumber = linensByType.size,
                        secondNumber = linensByType.count { remoteMarkers.contains(it.rfid) },
                        fontColor = fontColor
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun Preview1() {

    MobileAppAndroidForTerminalTheme {
        TypeLinenScreen(
            remoteMarkers = markers,
            size = linens.size,
            sortedGroups = linens3.groupBy { it.typeLinen }
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun Preview3() {
    val filterByType = FilterByType.FILTER_BY_TYPE_LINEN
    val sg = linens3.groupBy { it.typeLinen }

    val finishFilterGroups = when (filterByType) {
        FilterByType.ALL -> sg
        FilterByType.FILTER_BY_TYPE_LINEN -> {
            sg.filterKeys { key->
                key == typeLinen2
            }
        }
    }
    MobileAppAndroidForTerminalTheme {
        TypeLinenScreen(
            remoteMarkers = markers,
            size = linens.size,
            sortedGroups =finishFilterGroups
        )
    }
}
