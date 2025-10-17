package by.liscode.search

import by.liscode.domain.model.Client
import by.liscode.domain.model.GroupLinen
import by.liscode.domain.model.Linen

data class SearchScreenState(
    val isLoading: Boolean = false,
    val markers: List<String> = emptyList(),
    val remoteLinens: List<Linen> = emptyList(),
    val isActiveScanner: Boolean = false,
    val groupLinen: GroupLinen = GroupLinen.NAME,
    val searchState: SearchState = SearchState.SEARCH,
    val filterByType: FilterByType = FilterByType.All,
    val filterRfid: String = "",
    val clients: List<Client> =emptyList(),
) {
    val filteredLinen = when (filterByType) {
        FilterByType.All -> remoteLinens
        is FilterByType.FilterByMask -> {
            remoteLinens.filter { it.rfid.contains(filterRfid, ignoreCase = true) }
        }
    }
    val groupedLinenByClientId  = filteredLinen
        .groupBy { it.clientId }
    val groupedLinenByClientIdAndTypeLinen  = filteredLinen
        .groupBy { it.clientId }
        .mapValues { (_, linens) ->
            linens.groupBy { it.typeLinen }
        }
    val outMarkers = markers - remoteLinens.map { it.rfid }
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
    data object FilterByMask : FilterByType
}