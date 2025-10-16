package by.liscode.write_off


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.domain.model.Counterparty
import by.liscode.domain.model.Linen
import by.liscode.domain.model.StateLinen
import by.liscode.domain.model.StatusLinen
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.utils.printFormat
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.DividerSpacer
import by.liscode.resources.uikit.FundAppBar
import by.liscode.resources.uikit.OnScan
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.uikit.UiIconButton
import by.liscode.resources.uikit.UiTextField
import by.liscode.resources.uikit.asReasonText
import by.liscode.resources.uikit.rememberRfid
import by.liscode.resources.utils.applySettings
import by.liscode.resources.utils.processScan
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun WriteOffScreenRoot(
    viewModel: WriteOffScreenViewModel = koinViewModel(),
    onNavigateToLoginScreen: () -> Unit,
    onBackNavigate: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction
    var isOpenTypeReasonMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scanner = rememberRfid().apply { init(context) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiWriteOffEvents.LoginNavigationEvent -> onNavigateToLoginScreen()
            }
        }
    }
    LaunchedEffect(state.settings) {
        state.settings?.let {
            scanner.applySettings(it)
        }
    }
    OnScan(
        block = {
            processScan(
                rfid = scanner,
                isActive = state.isActiveScanner,
                block = {
                    onAction(WriteOffScreenAction.TakeMarker(
                        marker = it,
                        sound = R.raw.beep
                    ))
                },
                updateIsActive = {
                    onAction(WriteOffScreenAction.UpdateActivityScanner(it))
                }
            )
        },
        back = {
            scanner.free()
            onBackNavigate ()
        }
    )

    WriteOffScreenScreen(
        state = state,
        onAction = onAction,
        isOpenTypeReasonMenu = isOpenTypeReasonMenu,
        onBackNavigate = {
            scanner.free()
            onBackNavigate ()
        },
        updateVisibilityTypeReasonMenu = {
            isOpenTypeReasonMenu = it
        }
    )
}

@Composable
fun WriteOffScreenScreen(
    modifier: Modifier = Modifier,
    state: WriteOffScreenState,
    isOpenTypeReasonMenu: Boolean,
    onAction: (WriteOffScreenAction) -> Unit,
    onBackNavigate: () -> Unit,
    updateVisibilityTypeReasonMenu: (Boolean) -> Unit
) {
    if (state.isShowSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                onAction(WriteOffScreenAction.CloseSuccessDialog)
            },
            title = {
                Text(
                    text = stringResource(R.string.linen_success_write_off),
                    color = colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                Button(onClick = {
                    onAction(WriteOffScreenAction.CloseSuccessDialog)
                }) {
                    Text(
                        text = stringResource(R.string.ok),
                        color = colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

            },
        )
    }

    if (state.isShowDialog) {
        Dialog(
            onDismissRequest = {}
        ) {
            WarningDialogContent(
                confirmUpdate = {
                    onAction(WriteOffScreenAction.RemoveAndCloseVisibilityDialog)
                },
                cancelUpdate = {
                    onAction(WriteOffScreenAction.CloseVisibilityDialog)
                }
            )
        }
    }

    UiBaseScaffold(
        message = state.message,
        topBarContent = {
            FundAppBar(
                title = stringResource(R.string.write_off_linen),
                endContent = {
                    Text(
                        modifier = modifier.clickable(
                            enabled = state.markers.isNotEmpty(),
                            onClick = {
                                onAction(WriteOffScreenAction.SendWriteOff)
                            }
                        ),
                        text = stringResource(R.string.send),
                        color = colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                onBack = onBackNavigate
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(colorScheme.background)
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
                    .background(colorScheme.background)
                    .padding(15.dp),
            ) {
                OutlinedButton(
                    modifier = modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (state.isShowInfo) Color.Transparent else colorScheme.onPrimaryContainer
                    ),
                    onClick = {
                        onAction(WriteOffScreenAction.UpdateVisibilityInfo)
                    }
                ) {
                    Text(
                        modifier = modifier,
                        text = if (state.isShowInfo) stringResource(R.string.hide_info) else stringResource(
                            R.string.show_info
                        ),
                        color = if (state.isShowInfo) colorScheme.onPrimaryContainer else colorScheme.primaryContainer,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Spacer(modifier = modifier.height(10.dp))
                Text(
                    modifier = modifier,
                    text = "${stringResource(R.string.reason_write_off)}:",
                    color = colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = modifier.height(8.dp))
                Box {
                    Text(
                        modifier = modifier.clickable(
                            onClick = {
                                updateVisibilityTypeReasonMenu(true)
                            }
                        ),
                        text = state.reasonWriteOff.asReasonText().asString(),
                        color = colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        ),
                    )
                    DropdownMenu(
                        expanded = isOpenTypeReasonMenu,
                        onDismissRequest = {
                            updateVisibilityTypeReasonMenu(false)
                        }
                    ) {
                        state.reasons.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        modifier = modifier,
                                        text = it.asReasonText().asString(),
                                        color = colorScheme.onBackground,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                },
                                onClick = {
                                    onAction(WriteOffScreenAction.UpdateReasonWriteOff(it))
                                    updateVisibilityTypeReasonMenu(false)
                                }
                            )
                        }
                    }
                }
                DividerSpacer()
                Text(
                    modifier = modifier,
                    text = "${stringResource(R.string.ark_write_off)}:",
                    color = colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                )
                UiTextField(
                    content = state.actWriteOff,
                    placeholder = stringResource(R.string.number_act),
                    onClickContent = {
                        onAction(WriteOffScreenAction.UpdateActWriteOff(it))
                    }
                )
               DividerSpacer()
                Text(
                    modifier = modifier,
                    text = "${stringResource(R.string.all_reader_markers)} - ${state.markers.size}",
                    color = colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )
                if (state.isShowInfo) {
                    Spacer(modifier = modifier.height(10.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        items(state.markers) { marker ->
                            Row(
                                modifier = modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column (
                                    modifier = modifier.weight(1f)
                                ){
                                    val currentLinen = state.remoteLinens.firstOrNull { it.rfid == marker }
                                    currentLinen?.let { linen->
                                        Text(
                                            modifier = modifier,
                                            text = linen.printFormat(),
                                            color = colorScheme.onBackground,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                            ),
                                        )
                                    }
                                    Text(
                                        modifier = modifier,
                                        text = marker,
                                        color = colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                                UiIconButton(
                                    icon = ImageVector.vectorResource(R.drawable.outline_delete_24),
                                    onClick = {
                                        onAction(WriteOffScreenAction.RemoveMarker(marker))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview() {
    MobileAppAndroidForTerminalTheme {
        WriteOffScreenScreen(
            state = WriteOffScreenState(
                isShowDialog = true
            ),
            isOpenTypeReasonMenu = false,
            updateVisibilityTypeReasonMenu = {},
            onAction = {},
            onBackNavigate = {}
        )
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview1() {
    val typeLinen = TypeLinen(
        id = 1,
        name = "Пододеяльник",
        size = "200x220",
        fabric = "сатин страйп 3х3",
        composition = "",
        color = "белый",
        density = "",
        sku = "100% хлопок",
        maxWash = 10,
    )
    val markers = (1..10).map { "Marker N$it" }
    val linens = (3..7).map {
        Linen(
            id = it,
            clientId = 0,
            rfid = "Marker N$it",
            typeId = it + 3,
            typeLinen = typeLinen,
            discount = "",
            weight = 135,
            state = StateLinen.SATISFACTORY,
            washCount = 9,
            status = StatusLinen.IN_STOCK,
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
    MobileAppAndroidForTerminalTheme {
        WriteOffScreenScreen(
            state = WriteOffScreenState(
                isShowDialog = false,
                markers = markers,
                remoteLinens = linens
            ),
            isOpenTypeReasonMenu = false,
            updateVisibilityTypeReasonMenu = {},
            onAction = {},
            onBackNavigate = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview2() {
    val typeLinen = TypeLinen(
        id = 1,
        name = "Пододеяльник",
        size = "200x220",
        fabric = "сатин страйп 3х3",
        composition = "",
        color = "белый",
        density = "",
        sku = "100% хлопок",
        maxWash = 10,
    )
    val markers = (1..10).map { "Marker N$it" }
    val linens = (3..7).map {
        Linen(
            id = it,
            clientId = 0,
            rfid = "Marker N$it",
            typeId = it + 3,
            typeLinen = typeLinen,
            discount = "",
            weight = 135,
            state = StateLinen.SATISFACTORY,
            washCount = 9,
            status = StatusLinen.IN_STOCK,
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
    MobileAppAndroidForTerminalTheme {
        WriteOffScreenScreen(
            state = WriteOffScreenState(
                isShowDialog = false,
                isShowInfo = false,
                markers = markers,
                remoteLinens = linens
            ),
            isOpenTypeReasonMenu = true,
            updateVisibilityTypeReasonMenu = {},
            onAction = {},
            onBackNavigate = {}
        )
    }
}