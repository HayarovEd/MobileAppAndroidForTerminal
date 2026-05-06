package by.liscode.domain.utils

sealed interface DataError : RootError {

    class CustomError(val message: String): DataError

    enum class Network: DataError {
        NO_INTERNET,
        UNAUTHORIZED,
        SERVER_ERROR,
        BAD_REQUEST,
        NOT_FOUND,
        REQUEST_TIMEOUT,
        NO_CONNECT_SERVER,
        FORBIDDEN,
        TOO_MANY_REQUESTS,
        SERVICE_UNAVAILABLE,
        UNKNOWN
    }

    enum class SerializationError: DataError {
       FORMAT_ERROR,
    }

    enum class PasswordError: DataError {
        PASSWORD_BLANK,
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