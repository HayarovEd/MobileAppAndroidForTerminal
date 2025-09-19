package ru.modulmoney.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.Linen
import by.liscode.domain.model.TypeLinen
import by.liscode.resources.R
import by.liscode.resources.mock.linens
import by.liscode.resources.mock.markers
import by.liscode.resources.mock.markers15_45
import by.liscode.resources.mock.typeLinen2
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.UiRowThreeElements
import by.liscode.resources.uikit.UiRowThreeText
import by.liscode.resources.uikit.UiRowTitleThreeElements

@Composable
fun TypeLinenScreen(
    modifier: Modifier = Modifier,
    size: Int,
    markers: List<String>,
    sortedGroups: Map<Boolean, Map<TypeLinen, List<Linen>>>,

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
            title = "",
            firstNumber =  size,
            secondNumber = markers.size,
        )
        Spacer(modifier = modifier.height(10.dp))
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            sortedGroups.keys.forEach { status ->
                val linensByType = sortedGroups[status]?.keys?.toList()?: emptyList()
                items(linensByType) { type->
                    val linens = sortedGroups[status]?.get(type)?.map { it.rfid }?: emptyList()
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        val fontColor = if (status) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        UiRowThreeElements(
                            title = type,
                            firstNumber = linens.count { element->  markers.contains(element) },
                            secondNumber = linens.size,
                            fontColor = fontColor
                        )
                    }
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
            markers = markers,
            size = linens.size,
            sortedGroups = linens.groupBy {
                it.rfid in markers
            }.mapValues { (_, linens)->
                linens
                    .groupBy { it.typeLinen }
            }
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun Preview3() {
    val filterByType = FilterByType.FilterByTypeLinen(typeLinen2)
    val sg = linens.groupBy {
        it.rfid in markers15_45
    }.mapValues { (_, linens)->
        linens
            .groupBy { it.typeLinen }
    }
    val finishFilterGroups = when  (filterByType) {
        FilterByType.All -> sg
        is FilterByType.FilterByTypeLinen -> {
            sg.mapValues { (_, innerMap) ->
                innerMap.filter { (key, _) ->
                    key == filterByType.typeLinen
                }
            }.filterValues { it.isNotEmpty() }
        }
    }
    MobileAppAndroidForTerminalTheme {
        TypeLinenScreen(
            markers = markers,
            size = linens.size,
            sortedGroups =finishFilterGroups
        )
    }
}
