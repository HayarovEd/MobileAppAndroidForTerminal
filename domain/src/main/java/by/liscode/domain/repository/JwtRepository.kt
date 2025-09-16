package by.liscode.domain.repository

import by.liscode.domain.model.UserData

interface JwtRepository {
    fun decodeJwt(token: String): UserData?
}