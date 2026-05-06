package by.liscode.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.model.UserData
import by.liscode.domain.model.UserRole
import by.liscode.domain.model.UserType
import by.liscode.resources.R
import by.liscode.resources.mock.typeLinen1
import by.liscode.resources.mock.typeLinen2
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.DividerSpacer
import by.liscode.resources.uikit.UiRadioButtonRow
import by.liscode.resources.uikit.UiTextField
import by.liscode.resources.uikit.asStateLinenText

@Composable
fun FilteringScreen(
    modifier: Modifier = Modifier,
    userData: UserData?,
    filterByType: FilterByType,
    filterRfid: String,
    currentTypeLinen: TypeLinen?,
    typesLinen: List<TypeLinen>,
    updateFilter: (FilterByType) -> Unit,
    updateFilterRfid: (String) -> Unit,
    updateCurrentType: (TypeLinen) -> Unit
) {
    var isExpandedMenu by remember { mutableStateOf(false) }
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
        DividerSpacer()
        UiRadioButtonRow(
            title = stringResource(R.string.show_all),
            selected = filterByType is FilterByType.All,
            onClick = {
                updateFilter(FilterByType.All)
            }
        )
        userData?.let { user ->
            if (user.userType == UserType.HOTEL && typesLinen.isNotEmpty()) {
                DividerSpacer()
                UiRadioButtonRow(
                    title = stringResource(R.string.on_type_lineb),
                    selected = filterByType is FilterByType.FilterByTypeLinen,
                    onClick = {
                        updateFilter(FilterByType.FilterByTypeLinen)
                    }
                )
                Spacer(modifier = modifier.height(4.dp))
                Box {
                    Text(
                        modifier = modifier.clickable(
                            onClick = {
                                isExpandedMenu = true
                            }
                        ),
                        text = currentTypeLinen?.name?:typesLinen.first().name,
                        color = colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        ),
                    )
                    DropdownMenu(
                        expanded = isExpandedMenu,
                        onDismissRequest = {
                            isExpandedMenu = false
                        }
                    ) {
                        typesLinen.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        modifier = modifier,
                                        text = it.name,
                                        color = colorScheme.onBackground,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                },
                                onClick = {
                                    updateCurrentType(it)
                                    isExpandedMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
        DividerSpacer()
        UiRadioButtonRow(
            title = stringResource(R.string.on_rfid_marker),
            selected = filterByType is FilterByType.FilterByMask,
            onClick = {
                updateFilter(FilterByType.FilterByMask)
            }
        )
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
    MobileAppAndroidForTerminalTheme {
        FilteringScreen(
            filterRfid = "",
            userData = UserData(
                userId = 1,
                login = "rm",
                userRole = UserRole.CLIENT_EMPLOYEE,
                userType = UserType.LAUNDRY,
                expired = 0L
            ),
            currentTypeLinen = null,
            typesLinen = listOf(typeLinen1, typeLinen2),
            filterByType = FilterByType.All,
            updateFilter = {},
            updateFilterRfid = {},
            updateCurrentType = {},
        )
    }
}

@Preview
@Composable
private fun Preview2() {
    MobileAppAndroidForTerminalTheme {
        FilteringScreen(
            filterRfid = "Marker N5",
            userData = UserData(
                userId = 1,
                login = "rm",
                userRole = UserRole.CLIENT_EMPLOYEE,
                userType = UserType.HOTEL,
                expired = 0L
            ),
            currentTypeLinen = typeLinen1,
            typesLinen = listOf(typeLinen1, typeLinen2),
            filterByType = FilterByType.FilterByTypeLinen,
            updateFilter = {},
            updateFilterRfid = {},
            updateCurrentType = {}
        )
    }
}


@Preview
@Composable
private fun Preview3() {
    MobileAppAndroidForTerminalTheme {
        FilteringScreen(
            filterRfid = "Marker N5",
            userData = UserData(
                userId = 1,
                login = "rm",
                userRole = UserRole.CLIENT_EMPLOYEE,
                userType = UserType.HOTEL,
                expired = 0L
            ),
            currentTypeLinen = typeLinen1,
            typesLinen = listOf(typeLinen1, typeLinen2),
            filterByType = FilterByType.FilterByMask,
            updateFilter = {},
            updateFilterRfid = {},
            updateCurrentType = {}
        )
    }
}