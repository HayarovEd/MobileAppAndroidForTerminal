package by.liscode.domain.utils

sealed interface DataError : RootError {
    enum class Network: DataError {
        NO_INTERNET,
        UNAUTHORIZED,
        SERVER_ERROR,
        BAD_REQUEST,
        NOT_FOUND,
        REQUEST_TIMEOUT,
        REQUEST_CONFLICT,
        UNKNOWN
    }

    enum class SerializationError: DataError {
       FORMAT_ERROR,
    }

    enum class PasswordError: DataError {
        PASSWORD_BLANK,
        PASSWORDS_NOT_EQUAL,
    }

    enum class  DataStore: DataError {
        ERROR_READ_DATA,
        ERROR_WRITE_DATA
    }

    enum class LocalDateBase : DataError {
        ERROR_READ_DATA,
        ERROR_WRITE_DATA
    }

    enum class RegisterError: DataError {
        INCORRECT_CREDINTIAL
    }
}