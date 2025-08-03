package by.liscode.domain.model

import by.liscode.domain.utils.ISO18006C

enum class RfidProtocol(
    val title: String,
    val value: Int
) {
    ISO180006C(title = ISO18006C, value = 0),
    //ISO180006D(title = ISO18006D, value = 3)
}