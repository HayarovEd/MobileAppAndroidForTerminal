package by.liscode.domain.utils

sealed interface InfoMessage: RootError {
    enum class RFIDWork: InfoMessage {
        STOPPED,
        STARTED
    }
}