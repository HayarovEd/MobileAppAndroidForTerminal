package by.liscode.search

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.liscode.domain.model.GroupLinen
import by.liscode.resources.R
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import by.liscode.resources.uikit.FundAppBar
import by.liscode.resources.uikit.OnScan
import by.liscode.resources.uikit.UiBaseScaffold
import by.liscode.resources.uikit.UiIconButton
import by.liscode.resources.uikit.rememberRfid
import by.liscode.resources.utils.BaseState
import by.liscode.resources.utils.applySettings
import by.liscode.resources.utils.processScan
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreenRoot(
    viewModel: SearchScreenViewModel = koinViewModel(),
    onNavigateToLoginScreen: () -> Unit,
    onBackNavigate: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val baseState by viewModel.baseState.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction
    val context = LocalContext.current
    val scanner = rememberRfid().apply { init(context) }
    BackHandler { onBackNavigate() }
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiSearchEvents.LoginNavigationEvent -> onNavigateToLoginScreen()
            }
        }
    }
    LaunchedEffect(baseState.settings) {
        baseState.settings?.let {
            scanner.applySettings(it)
        }
    }
    OnScan(
        block = {
            processScan(
                rfid = scanner,
                isActive = state.isActiveScanner,
                block = {
                    onAction(
                        SearchScreenAction.TakeMarker(
                            marker = it,
                            sound = R.raw.beep
                        )
                    )
                },
                updateIsActive = {
                    onAction(SearchScreenAction.UpdateActivityScanner(it))
                }
            )
        },
        back = {
            scanner.free()
            onBackNavigate()
        }
    )
    SearchScreenScreen(
        state = state,
        baseState = baseState,
        onAction = viewModel::onAction,
        onBackNavigate = {
            scanner.free()
            onBackNavigate()
        }
    )
}

@Composable
fun SearchScreenScreen(
    modifier: Modifier = Modifier,
    state: SearchScreenState,
    baseState: BaseState,
    onAction: (SearchScreenAction) -> Unit,
    onBackNavigate: () -> Unit
) {
    UiBaseScaffold(
        message = baseState.message,
        topBarContent = {
            when (state.searchState) {
                SearchState.SEARCH -> {
                    FundAppBar(
                        title = stringResource(R.string.search),
                        endContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = modifier.clickable(
                                        enabled = state.markers.isNotEmpty(),
                                        onClick = {
                                            onAction(SearchScreenAction.UpdateGroupping)
                                        }
                                    ),
                                    text = stringResource(R.string.kind_upper_case),
                                    color = colorScheme.onPrimary,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                UiIconButton(
                                    icon = Icons.Default.MoreVert,
                                    color = colorScheme.onPrimary,
                                    onClick = {
                                        onAction(SearchScreenAction.UpdateSearchState(SearchState.FILTERING))
                                    }
                                )
                            }
                        },
                        onBack = onBackNavigate
                    )
                }

                SearchState.FILTERING -> {
                    FundAppBar(
                        title = stringResource(R.string.filter),
                        onBack = {
                            onAction(SearchScreenAction.UpdateSearchState(SearchState.SEARCH))
                        }
                    )
                }
            }
        },
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
                when (state.searchState) {
                    SearchState.SEARCH -> {
                        Row(
                            modifier = modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = modifier,
                                text = "${stringResource(R.string.remain_download)}: ${state.markers.size - state.remoteLinens.size}",
                                color = colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                modifier = modifier,
                                text = "${stringResource(R.string.scanned)}: ${state.markers.size}",
                                color = colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        Spacer(modifier = modifier.height(10.dp))
                        when (state.groupLinen) {
                            GroupLinen.NAME -> {
                                TypeNameScreen(
                                    groupedLinen = state.groupedLinenByClientId,
                                    outMarkers = state.outMarkers,
                                    clients = state.clients
                                )
                            }

                            GroupLinen.TYPE_LINEN -> {
                                TypeLinenScreen(
                                    groupedLinen = state.groupedLinenByClientIdAndTypeLinen,
                                    outMarkers = state.outMarkers,
                                    clients = state.clients
                                )
                            }
                        }
                    }

                    SearchState.FILTERING -> {
                        FilteringScreen(
                            filterRfid = state.filterRfid,
                            filterByType = state.filterByType,
                            typesLinen = state.typesLinen,
                            userData = state.userData,
                            currentTypeLinen = state.currentType,
                            updateFilter = {
                                onAction(SearchScreenAction.UpdateFilter(it))
                            },
                            updateFilterRfid = {
                                onAction(SearchScreenAction.UpdateFilterRfid(it))
                            },
                            updateCurrentType = {
                                onAction(SearchScreenAction.UpdateCurrentType(it))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview1() {
    MobileAppAndroidForTerminalTheme {
        SearchScreenScreen(
            state = SearchScreenState(),
            baseState = BaseState(),
            onAction = {},
            onBackNavigate = {}
        )
    }
}

@Preview
@Composable
private fun Preview2() {
    MobileAppAndroidForTerminalTheme {
        SearchScreenScreen(
            state = SearchScreenState(
                searchState = SearchState.FILTERING
            ),
            baseState = BaseState(),
            onAction = {},
            onBackNavigate = {}
        )
    }
}

