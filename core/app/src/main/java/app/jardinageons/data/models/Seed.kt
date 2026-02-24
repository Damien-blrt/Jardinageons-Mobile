package app.jardinageons.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Seed(
    val id: Long,
    val name: String,
    val quantity: Int,
    val germinationTime: Int,
    val description: String,
    val vegetableId: Long?,
    var expiryDate: String
)
