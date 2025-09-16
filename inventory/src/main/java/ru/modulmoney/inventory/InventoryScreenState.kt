package ru.modulmoney.inventory

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
    val filterByType: FilterByType = FilterByType.All,
    val groupLinen: GroupLinen = GroupLinen.NAME,
    val isActiveScanner: Boolean = false,
    val filterMarkers: List<Filter> = emptyList(),
    val inventoryState: InventoryState = InventoryState.CHECKING
) {
    val linenTypes = remoteLinens.map { it.typeLinen }.distinct()
    private val sortedLinens = remoteLinens.groupBy {
        it.rfid in markers
    }
        .mapValues { (_, linens) ->
            linens.sortedBy { it.typeLinen.outFormat() }
        }

    val finishSortedLines = when  (filterByType) {
        FilterByType.All -> sortedLinens
        is FilterByType.FilterByTypeLinen -> {
            sortedLinens.mapValues { (_, innerMap) ->
                innerMap.filter { it.typeLinen == filterByType.typeLinen}
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
        FilterByType.All -> sortedGroups
        is FilterByType.FilterByTypeLinen -> {
            sortedGroups.mapValues { (_, innerMap) ->
                innerMap.filter { (key, _) ->
                    key == filterByType.typeLinen
                }
            }.filterValues { it.isNotEmpty() }
        }
    }
}

sealed class UiInventoryEvents {
    data object LoginNavigationEvent : UiInventoryEvents()
}

sealed interface FilterByType {
    data object All : FilterByType
    class FilterByTypeLinen(val typeLinen: TypeLinen) : FilterByType
}


enum class InventoryState {
    CHECKING,
    FILTERING
}