package by.liscode.domain.repository

import by.liscode.domain.model.Auth
import by.liscode.domain.model.Client
import by.liscode.domain.utils.DataError
import by.liscode.domain.utils.ResultWork

interface AuthorizeRepository {
    suspend fun authorization(
        login: String,
        password: String,
        baseUrl: String
    ): ResultWork<Auth, DataError>

    suspend fun getProfile(baseUrl: String, token: String): ResultWork<List<Client>, DataError>
}