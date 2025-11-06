package com.edurda77.take_work

import by.liscode.domain.model.GroupLinen
import by.liscode.domain.model.Linen

data class TakeWorkScreenState(
    val isLoading: Boolean = false,
    val markers: List<String> = emptyList(),
    val remoteLinens: List<Linen> = emptyList(),
    val groupLinen: GroupLinen = GroupLinen.NAME,
    val isShowResultDialog: Boolean = false,
    val isShowWarning: Boolean = false,
    val isActiveScanner: Boolean = false,
) {
    val sortedGroups = remoteLinens.groupBy {
        it.typeLinen
    }
}

sealed class UiTakeWorkEvents {
    data object LoginNavigationEvent : UiTakeWorkEvents()
}
