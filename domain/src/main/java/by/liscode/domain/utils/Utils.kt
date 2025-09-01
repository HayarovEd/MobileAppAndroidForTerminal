package by.liscode.domain.utils

import by.liscode.domain.model.UserRole


fun fliterByListString(
    target: List<String>,
    etalon: List<String>,
): List<String> {
    return target.filter { itemA ->
        etalon.any { prefix -> itemA.startsWith(prefix, ignoreCase = true) }
    }
}


fun convertToUserRole(role: String): UserRole {
    return when (role)
}
