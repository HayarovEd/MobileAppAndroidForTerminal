package by.liscode.domain.utils


sealed interface ResultWork<out D, out E: RootError> {
    data class Success<out D, out E: RootError>(val data: D): ResultWork<D, E>
    data class Error<out D, out E: RootError>(val error: E): ResultWork<D, E>

}

inline fun <T, E: RootError, R> ResultWork<T, E>.map(map: (T) -> R): ResultWork<R, E> {
    return when(this) {
        is ResultWork.Error -> ResultWork.Error(error)
        is ResultWork.Success -> ResultWork.Success(map(this.data))
    }
}

inline fun <T, E: RootError> ResultWork<T, E>.onSuccess(action: (T) -> Unit): ResultWork<T, E> {
    return when(this) {
        is ResultWork.Error -> this
        is ResultWork.Success -> {
            action(this.data)
            this
        }
    }
}

inline fun <T, E: RootError> ResultWork<T, E>.onFailure(action: (E) -> Unit): ResultWork<T, E> {
    return when(this) {
        is ResultWork.Error -> {
            action(error)
            this
        }
        is ResultWork.Success -> this
    }
}

fun <T, E: RootError> ResultWork<T, E>.asEmptyResult(): EmptyResult<E> {
    return map {  }
}

typealias EmptyResult<E> = ResultWork<Unit, E>