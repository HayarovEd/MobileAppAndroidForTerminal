package by.liscode.domain.repository

import by.liscode.domain.model.Auth
import by.liscode.domain.utils.DataError
import com.nine.statio.domain.utils.ResultWork

interface AuthorizeRepository {
    suspend fun authorization(
        login: String,
        password: String,
        baseUrl: String
    ): ResultWork<Auth, DataError>
}