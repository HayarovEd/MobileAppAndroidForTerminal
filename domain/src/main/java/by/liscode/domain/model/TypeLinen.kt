package by.liscode.domain.model

data class TypeLinen(
    val id: Int,
    val name: String,
    val size: String,
    val fabric: String,
    val composition: String,
    val color: String,
    val density: String,
    val sku: String,
    val maxWash: Int,
    val clientId:Int,
)
