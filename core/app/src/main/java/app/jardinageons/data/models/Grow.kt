package app.jardinageons.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Grow(
    val id: Long,
    val vegetableId: Long,
    val plantingDate: String? = null,
    val quantity: Int? = 1
)