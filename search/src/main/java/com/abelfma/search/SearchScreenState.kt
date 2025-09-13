package com.abelfma.search

import by.liscode.domain.model.Client
import by.liscode.domain.model.Filter
import by.liscode.domain.model.GroupLinen
import by.liscode.domain.model.Linen
import by.liscode.domain.model.RfidSetting
import by.liscode.resources.uikit.UiText

data class SearchScreenState(
    val client: Client? = null,
    val isLoading: Boolean = false,
    val settings: RfidSetting? = null,
    val message: UiText? = null,
    val markers: List<String> = emptyList(),
    val remoteLinens: List<Linen> = emptyList(),
    val filterMarkers: List<Filter> = emptyList(),
    val isActiveScanner: Boolean = false,
    val groupLinen: GroupLinen = GroupLinen.NAME,
    val searchState: SearchState = SearchState.SEARCH,
    val filterByType: FilterByType = FilterByType.All
) {
    val filteredLinen = when (filterByType) {
        FilterByType.All -> remoteLinens
        FilterByType.ClientId -> client?.let { clt->
            remoteLinens.filter { it.typeLinen.clientId == clt.id}
        }?: remoteLinens
        is FilterByType.FilterByMask -> {
            remoteLinens.filter { it.rfid == filterByType.mask }
        }
    }
    val grouppedLinenByClientId  = filteredLinen
        .groupBy { it.typeLinen.clientId }
    val grouppedLinenByClientIdAndTypeLinen  = filteredLinen
        .groupBy { it.typeLinen.clientId }
        .mapValues { (_, linens) ->
            linens.groupBy { it.typeLinen }
        }
}

sealed class UiSearchEvents {
    data object LoginNavigationEvent : UiSearchEvents()
}


enum class SearchState {
    SEARCH,
    FILTERING
}

sealed interface FilterByType {
    data object All : FilterByType
    data object ClientId: FilterByType
    class FilterByMask(val mask: String) : FilterByType
}