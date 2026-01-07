package by.liscode.resources.uikit

import by.liscode.domain.utils.DataError
import by.liscode.resources.R
import by.liscode.resources.uikit.UiText.StringResource
import by.liscode.domain.utils.DataError.DataStore.ERROR_READ_DATA
import by.liscode.domain.utils.DataError.DataStore.ERROR_WRITE_DATA
import by.liscode.domain.utils.DataError.LocalDateBase
import by.liscode.domain.utils.DataError.Network.BAD_REQUEST
import by.liscode.domain.utils.DataError.Network.NOT_FOUND
import by.liscode.domain.utils.DataError.Network.NO_INTERNET
import by.liscode.domain.utils.DataError.Network.REQUEST_TIMEOUT
import by.liscode.domain.utils.DataError.Network.SERVER_ERROR
import by.liscode.domain.utils.DataError.Network.UNAUTHORIZED
import by.liscode.domain.utils.DataError.Network.UNKNOWN
import by.liscode.domain.utils.DataError.PasswordError.PASSWORD_BLANK
import by.liscode.domain.utils.DataError.SerializationError.FORMAT_ERROR
import by.liscode.domain.utils.InfoMessage
import com.nine.statio.domain.utils.ResultWork

fun DataError.asUiText(): UiText {
    return when (this) {
        NO_INTERNET -> StringResource(
            R.string.no_internet
        )

        SERVER_ERROR -> StringResource(
            R.string.server_error
        )

        UNKNOWN -> StringResource(
            R.string.unknown_error
        )

        UNAUTHORIZED -> StringResource(
            R.string.error_autorization
        )
        PASSWORD_BLANK -> StringResource(
            R.string.password_blank
        )

        BAD_REQUEST -> {
            StringResource(
                R.string.not_unique_id
            )
        }
        ERROR_READ_DATA -> {
            StringResource(
                R.string.read_error_data
            )
        }

        ERROR_WRITE_DATA -> {
            StringResource(
                R.string.write_error_data
            )
        }
        NOT_FOUND -> {
            StringResource(
                R.string.not_found
            )
        }
        FORMAT_ERROR -> {
            StringResource(
                R.string.error_take_data
            )
        }

        LocalDateBase.ERROR_READ_DATA -> {
            StringResource(
                R.string.read_error_data
            )
        }

        LocalDateBase.ERROR_WRITE_DATA -> {
            StringResource(
                R.string.read_error_data
            )
        }

        DataError.RegisterError.INCORRECT_CREDINTIAL -> {
            StringResource(
                R.string.incorrect_credintial
            )
        }

        REQUEST_TIMEOUT -> {
            StringResource(
                R.string.request_timeout
            )
        }

        DataError.Network.NO_CONNECT_SERVER -> StringResource(
            R.string.no_connect_server
        )

        DataError.Network.FORBIDDEN -> StringResource(
            R.string.no_access_to_host
        )
        DataError.Network.TOO_MANY_REQUESTS -> StringResource(
            R.string.to_many_requests
        )
        DataError.Network.SERVICE_UNAVAILABLE -> StringResource(
            R.string.no_connect_server
        )
    }
}

fun InfoMessage.asText() : UiText {
    return when (this) {
        InfoMessage.RFIDWork.STOPPED ->  StringResource(
            R.string.scan_stopped
        )
        InfoMessage.RFIDWork.STARTED -> {
            StringResource(
                R.string.scan_started
            )
        }
    }
}

fun ResultWork.Error<*, DataError>.asErrorUiText(): UiText {
    return error.asUiText()
}