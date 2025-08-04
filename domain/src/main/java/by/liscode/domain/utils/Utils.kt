package by.liscode.domain.utils


fun fliterByListString(
    target: List<String>,
    etalon: List<String>,
): List<String> {
    return target.filter { itemA ->
        etalon.any { prefix -> itemA.startsWith(prefix, ignoreCase = true) }
    }
}