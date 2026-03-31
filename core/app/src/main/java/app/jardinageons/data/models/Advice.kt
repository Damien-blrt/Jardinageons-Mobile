package app.jardinageons.data.models

data class Advice(
    val id: Int,
    val titre: String,
    val advice: String,
    val month: String? = null
)