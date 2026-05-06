package by.liscode.inventory

import by.liscode.domain.model.GroupLinen
import by.liscode.domain.model.Linen
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.utils.outFormat

data class InventoryScreenState(
    val isLoading: Boolean = false,
    val markers: List<String> = emptyList(),
    val remoteLinens: List<Linen> = emptyList(),
    val filterByType: FilterByType = FilterByType.ALL,
    val groupLinen: GroupLinen = GroupLinen.NAME,
    val isActiveScanner: Boolean = false,
    val inventoryState: InventoryState = InventoryState.CHECKING,
    val currentTypeLinen: TypeLinen? = null,
    val isShowSuccessDialog: Boolean = false,
) {
    val linenTypes = remoteLinens.map { it.typeLinen }.distinct()
    private val sortedLinens = remoteLinens.groupBy {
        it.rfid in markers
    }.toSortedMap(compareBy { it })
        .mapValues { (_, linens) ->
            linens.sortedBy { it.typeLinen.outFormat() }
        }

    val finishSortedLines = when (filterByType) {
        FilterByType.ALL -> sortedLinens
        FilterByType.FILTER_BY_TYPE_LINEN -> {
            sortedLinens.mapValues { (_, innerMap) ->
                innerMap.filter { it.typeLinen == currentTypeLinen }
            }.filterValues { it.isNotEmpty() }
        }
    }
    /*private val sortedGroups = remoteLinens.groupBy {
        it.rfid in markers
    }.toSortedMap(compareBy { it })
        .mapValues { (_, linens) ->
            linens
                .groupBy { it.typeLinen }
        }*/
    private val sortedGroups = remoteLinens.groupBy { it.typeLinen }

    val finishFilterGroups = when (filterByType) {
        FilterByType.ALL -> sortedGroups
        FilterByType.FILTER_BY_TYPE_LINEN -> {
            sortedGroups.filterKeys { key->
                key == currentTypeLinen
            }
        }
    }
}

sealed class UiInventoryEvents {
    data object LoginNavigationEvent : UiInventoryEvents()
}

enum class FilterByType {
    ALL,
    FILTER_BY_TYPE_LINEN
}


enum class InventoryState {
    CHECKING,
    FILTERING
}