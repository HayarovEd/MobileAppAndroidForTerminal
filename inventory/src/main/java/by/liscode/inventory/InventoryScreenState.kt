package by.liscode.inventory

import by.liscode.domain.model.Filter
import by.liscode.domain.model.GroupLinen
import by.liscode.domain.model.Linen
import by.liscode.domain.model.RfidSetting
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.utils.outFormat
import by.liscode.resources.uikit.UiText
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.sortedBy

data class InventoryScreenState(
    val settings: RfidSetting? = null,
    val message: UiText? = null,
    val isLoading: Boolean = false,
    val markers: List<String> = emptyList(),
    val remoteLinens: List<Linen> = emptyList(),
    val filterByType: FilterByType = FilterByType.ALL,
    val groupLinen: GroupLinen = GroupLinen.NAME,
    val isActiveScanner: Boolean = false,
    val filterMarkers: List<Filter> = emptyList(),
    val inventoryState: InventoryState = InventoryState.CHECKING,
    val currentTypeLinen: TypeLinen? = null,
) {
    val linenTypes = remoteLinens.map { it.typeLinen }.distinct()
    private val sortedLinens = remoteLinens.groupBy {
        it.rfid in markers
    }
        .mapValues { (_, linens) ->
            linens.sortedBy { it.typeLinen.outFormat() }
        }

    val finishSortedLines = when  (filterByType) {
        FilterByType.ALL -> sortedLinens
        FilterByType.FILTER_BY_TYPE_LINEN -> {
            sortedLinens.mapValues { (_, innerMap) ->
                innerMap.filter { it.typeLinen == currentTypeLinen}
            }.filterValues { it.isNotEmpty() }
        }
    }
    private val sortedGroups = remoteLinens.groupBy {
        it.rfid in markers
    }.mapValues { (_, linens)->
        linens
            .groupBy { it.typeLinen }
    }
    val finishFilterGroups = when  (filterByType) {
        FilterByType.ALL -> sortedGroups
        FilterByType.FILTER_BY_TYPE_LINEN -> {
            sortedGroups.mapValues { (_, innerMap) ->
                innerMap.filter { (key, _) ->
                    key == currentTypeLinen
                }
            }.filterValues { it.isNotEmpty() }
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