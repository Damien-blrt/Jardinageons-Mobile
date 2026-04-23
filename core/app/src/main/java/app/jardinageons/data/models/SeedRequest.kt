package app.jardinageons.data.models

data class SeedRequest(
    val name: String,
    val quantity: Int,
    val germinationTime: Int,
    val description: String,
    val vegetableId: Int,
    val expiryDate: String
)
