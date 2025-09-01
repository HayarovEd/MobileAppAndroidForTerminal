package by.liscode.data.repository

import android.util.Base64
import by.liscode.domain.model.UserData
import by.liscode.domain.repository.JwtRepository
import org.json.JSONObject

class JwtRepositoryImpl: JwtRepository {

    fun decodeJwt(token: String): UserData? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null

            val payload = decodeBase64(parts[1])
            val jsonObject = JSONObject(payload)
            val dataObject = jsonObject.getJSONObject("data")

            UserData(
                userId = dataObject.getInt("user_id"),
                login = dataObject.getString("login"),
                role = dataObject.getString("role")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun decodeBase64(encoded: String): String {
        val decodedBytes = Base64.decode(encoded, Base64.URL_SAFE or Base64.NO_PADDING)
        return String(decodedBytes, Charsets.UTF_8)
    }
}