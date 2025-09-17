package com.abelfma.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.UiTextField

@Composable
fun FilteringScreen(
    modifier: Modifier = Modifier,
    filterByType: FilterByType,
    filterRfid: String,
    updateFilter: (FilterByType) -> Unit,
    updateFilterRfid: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(15.dp),
    ) {
        Text(
            modifier = modifier,
            text = stringResource(R.string.choose_flter_type),
            color = colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
        )
        Spacer(modifier = modifier.height(4.dp))
        HorizontalDivider(
            modifier = modifier.fillMaxWidth(),
            thickness = 1.dp
        )
        Spacer(modifier = modifier.height(4.dp))
        Row(
            modifier = modifier
                .clickable(onClick = { updateFilter(FilterByType.All) }),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = filterByType is FilterByType.All,
                onClick = {
                   updateFilter(FilterByType.All)
                }
            )
            Text(
                modifier = modifier,
                text = stringResource(R.string.show_all),
                color = colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = modifier.height(4.dp))
        HorizontalDivider(
            modifier = modifier.fillMaxWidth(),
            thickness = 1.dp
        )
        /*Spacer(modifier = modifier.height(4.dp))
        Row(
            modifier = modifier.clickable(onClick = { updateFilter(FilterByType.ClientId) }),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = filterByType is FilterByType.ClientId,
                onClick = {
                    updateFilter(FilterByType.ClientId)
                }
            )
            Text(
                modifier = modifier,
                text = stringResource(R.string.individual_found_client),
                color = colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }*/
        Spacer(modifier = modifier.height(4.dp))
        HorizontalDivider(
            modifier = modifier.fillMaxWidth(),
            thickness = 1.dp
        )
        Spacer(modifier = modifier.height(4.dp))
        Row(
            modifier = modifier.clickable(onClick = { updateFilter(FilterByType.FilterByMask(filterRfid)) }),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = filterByType is FilterByType.FilterByMask,
                onClick = {
                    updateFilter(FilterByType.FilterByMask(filterRfid))
                }
            )
            Text(
                modifier = modifier,
                text = stringResource(R.string.on_rfid_marker),
                color = colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = modifier.height(4.dp))
        UiTextField(
            content = filterRfid,
            label = stringResource(R.string.enter_filter),
            onClickContent = {
                updateFilterRfid(it)
            }
        )
    }
}

@Preview
@Composable
private fun Preview1() {
    MobileAppAndroidForTerminalTheme{
        FilteringScreen(
            filterRfid = "",
            filterByType = FilterByType.All,
            updateFilter = {},
            updateFilterRfid = {}
        )
    }
}


@Preview
@Composable
private fun Preview3() {
    MobileAppAndroidForTerminalTheme{
        FilteringScreen(
            filterRfid = "Marker N5",
            filterByType = FilterByType.FilterByMask("Marker N5"),
            updateFilter = {},
            updateFilterRfid = {}
        )
    }
}