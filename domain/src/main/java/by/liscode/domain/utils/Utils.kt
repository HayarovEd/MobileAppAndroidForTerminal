package by.liscode.domain.utils

import by.liscode.domain.model.UserRole
import by.liscode.domain.model.UserType


fun fliterByListString(
    target: List<String>,
    etalon: List<String>,
): List<String> {
    return target.filter { itemA ->
        etalon.any { prefix -> itemA.startsWith(prefix, ignoreCase = true) }
    }
}


fun convertToUserRole(role: String): UserRole {
    return when (role) {
        "manager" -> UserRole.MANAGER
        "client" -> UserRole.CLIENT
        "client_employee" -> UserRole.CLIENT_EMPLOYEE
        else -> UserRole.UNKNOWN_ROLE
    }
}
fun convertToUserType(type: String): UserType {
    return when (type) {
        "hotel" -> UserType.HOTEL
        "laundry" -> UserType.LAUNDRY
        else -> UserType.UNKNOWN_TYPE
    }
}

