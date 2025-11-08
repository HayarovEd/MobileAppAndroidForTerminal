package by.liscode.take_balance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.utils.outFormat
import by.liscode.resources.R
import by.liscode.resources.mock.linens2
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.DividerSpacer
import by.liscode.resources.uikit.UiRowTitleThreeElements
import by.liscode.resources.uikit.UiTextField
import by.liscode.resources.uikit.asStateLinenText


@Composable
fun TakeBalanceAddScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    state: TakeBalanceScreenState,
    isOpenTypeLinenMenu: Boolean,
    isOpenedStateLinenMenu: Boolean,
    updateVisibleInfo: () -> Unit,
    updateCurrentTypeLinen: (TypeLinen) -> Unit,
    updateCurrentStateLinen: (StateLinen) -> Unit,
    updateVisibleTypeLinenMenu: (Boolean) -> Unit,
    updateVisibleStateLinenMenu: (Boolean) -> Unit,
    updateArticleNumber: (String) -> Unit,
    updateWeight: (String) -> Unit,
    updateBillLading: (String) -> Unit,
    updateStateScreen: (StateScreen) -> Unit,
    deleteMarker: (String) -> Unit,
) {
    if (state.isLoading) {
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
            OutlinedButton(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (state.isOpenInfo) Color.Transparent else MaterialTheme.colorScheme.onPrimaryContainer
                ),
                onClick = updateVisibleInfo
            ) {
                Text(
                    modifier = modifier,
                    text = if (state.isOpenInfo) stringResource(R.string.hide_info) else stringResource(
                        R.string.show_info
                    ),
                    color = if (state.isOpenInfo) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            if (state.isOpenInfo) {
                Spacer(modifier = modifier.height(10.dp))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = modifier,
                        text = "${stringResource(R.string.type)}:",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = modifier.width(5.dp))
                    Box {
                        Text(
                            modifier = modifier.clickable(
                                onClick = {
                                    updateVisibleTypeLinenMenu(true)
                                }
                            ),
                            text = if (state.currentTypeLinen == null) stringResource(R.string.choose) else state.currentTypeLinen.outFormat(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            ),
                        )
                        DropdownMenu(
                            expanded = isOpenTypeLinenMenu,
                            onDismissRequest = {
                                updateVisibleTypeLinenMenu(false)
                            }
                        ) {
                            state.typesLinen.forEach {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            modifier = modifier,
                                            text = it.outFormat(),
                                            color = MaterialTheme.colorScheme.onBackground,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    },
                                    onClick = {
                                        updateCurrentTypeLinen(it)
                                        updateVisibleTypeLinenMenu(false)
                                    }
                                )
                            }
                        }
                    }
                }
                DividerSpacer()
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = modifier,
                        text = "${stringResource(R.string.article)}:",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    UiTextField(
                        content = state.articleNumber,
                        placeholder = "",
                        imeAction = ImeAction.Next,
                        onClickContent = {
                            updateArticleNumber(it)
                        }
                    )
                }
                DividerSpacer()
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = modifier,
                        text = "${stringResource(R.string.state_linen)}:",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Box {
                        Text(
                            modifier = modifier.clickable(
                                onClick = {
                                    updateVisibleStateLinenMenu(true)
                                }
                            ),
                            text = state.currentStateLinen.asStateLinenText().asString(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            ),
                        )
                        DropdownMenu(
                            expanded = isOpenedStateLinenMenu,
                            onDismissRequest = {
                                updateVisibleStateLinenMenu(false)
                            }
                        ) {
                            state.statesLinen.forEach {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            modifier = modifier,
                                            text = it.asStateLinenText().asString(),
                                            color = MaterialTheme.colorScheme.onBackground,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    },
                                    onClick = {
                                        updateCurrentStateLinen(it)
                                        updateVisibleStateLinenMenu(false)
                                    }
                                )
                            }
                        }
                    }
                }
                DividerSpacer()
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = modifier,
                        text = "${stringResource(R.string.weight_one)}:",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    UiTextField(
                        modifier = modifier.weight(1f),
                        content = state.weight,
                        placeholder = "",
                        isOnlyDigit = true,
                        imeAction = ImeAction.Next,
                        onClickContent = {
                            updateWeight(it)
                        }
                    )
                    Text(
                        modifier = modifier,
                        text = "${stringResource(R.string.weight_unit)}.",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                DividerSpacer()
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = modifier,
                        text = "${stringResource(R.string.bill_lading)}:",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    UiTextField(
                        modifier = modifier.weight(1f),
                        content = state.billLading,
                        imeAction = ImeAction.Done,
                        placeholder = stringResource(R.string.bill_lading_label),
                        onClickContent = {
                            updateBillLading(it)
                        }
                    )
                }
                DividerSpacer()
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = modifier,
                        text = "${stringResource(R.string.counterparty)}:",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        modifier = modifier.clickable(
                            onClick = {
                                updateStateScreen(StateScreen.COUNTERPARTY)
                            }
                        ),
                        text = if (state.currentCounterparty == null) stringResource(R.string.choose) else state.currentCounterparty.name,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        ),
                    )
                }
            }
            DividerSpacer()
            Text(
                modifier = modifier,
                text = "${stringResource(R.string.all_reader_markers)} - ${state.markers.size}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = modifier.height(8.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(state.markersInBalance) { marker ->
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = modifier,
                            text = marker,
                            color = colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                }
                item {
                    Row(
                        modifier = modifier
                            .clip(shape = RoundedCornerShape(10.dp))
                            .fillMaxWidth()
                            .background(colorScheme.secondary)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = modifier.weight(6f),
                            text = stringResource(R.string.not_finded_markers),
                            color = colorScheme.onSecondary,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                items(state.markersNotBalance) { marker ->
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = modifier,
                            text = marker,
                            color = colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val counterParties = (1..5).map {
        Counterparty(
            id = it,
            name = "name $it",
            address = "Russia, Moscow, Baumana, $it",
            inn = "333-333-333-0$it",
            phone = "+7(555)55-55-222"
        )
    }
    val markers = (1..10).map {
        "marker 000$it"
    }
    MobileAppAndroidForTerminalTheme {
        TakeBalanceAddScreen(
            state = TakeBalanceScreenState(
                statesLinen = StateLinen.entries,
                counterparties = counterParties,
                markers = markers,
                remoteLinens = linens2,

            ),
            isOpenTypeLinenMenu = false,
            isOpenedStateLinenMenu = true,
            updateVisibleInfo = {},
            updateCurrentTypeLinen = {},
            updateCurrentStateLinen = {},
            updateVisibleTypeLinenMenu = {},
            updateVisibleStateLinenMenu = {},
            updateArticleNumber = {},
            updateWeight = {},
            updateBillLading = {},
            updateStateScreen = {},
            paddingValues = PaddingValues(10.dp),
            deleteMarker = {},
        )
    }
}

@Preview
@Composable
private fun Preview3() {
    val counterParties = (1..5).map {
        Counterparty(
            id = it,
            name = "name $it",
            address = "Russia, Moscow, Baumana, $it",
            inn = "333-333-333-0$it",
            phone = "+7(555)55-55-222"
        )
    }
    val typesLinen = (1..5).map {
        TypeLinen(
            id = it,
            name = "name $it",
            size = "${it * 100}",
            fabric = "",
            composition = "",
            color = "white",
            density = "100",
            sku = "",
            maxWash = 10,
        )
    }
    val markers = (1..10).map {
        "marker 000$it"
    }
    MobileAppAndroidForTerminalTheme {
        TakeBalanceAddScreen(
            state = TakeBalanceScreenState(
                statesLinen = StateLinen.entries,
                counterparties = counterParties,
                typesLinen = typesLinen,
                markers = markers,
                remoteLinens = linens2,
            ),
            isOpenTypeLinenMenu = false,
            isOpenedStateLinenMenu = false,
            updateVisibleInfo = {},
            updateCurrentTypeLinen = {},
            updateCurrentStateLinen = {},
            updateVisibleTypeLinenMenu = {},
            updateVisibleStateLinenMenu = {},
            updateArticleNumber = {},
            updateWeight = {},
            updateBillLading = {},
            updateStateScreen = {},
            paddingValues = PaddingValues(10.dp),
            deleteMarker = {},
        )
    }
}

@Preview
@Composable
private fun Preview2() {
    val markers = (1..10).map {
        "marker 000$it"
    }
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
        TakeBalanceAddScreen(
            state = TakeBalanceScreenState(
                statesLinen = StateLinen.entries,
                counterparties = counterParties,
                isOpenInfo = false,
                markers = markers,
                remoteLinens = linens2,
            ),
            isOpenTypeLinenMenu = false,
            isOpenedStateLinenMenu = false,
            updateVisibleInfo = {},
            updateCurrentTypeLinen = {},
            updateCurrentStateLinen = {},
            updateVisibleTypeLinenMenu = {},
            updateVisibleStateLinenMenu = {},
            updateArticleNumber = {},
            updateWeight = {},
            updateBillLading = {},
            updateStateScreen = {},
            paddingValues = PaddingValues(10.dp),
            deleteMarker = {},
        )
    }
}