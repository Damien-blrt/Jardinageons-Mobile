package app.jardinageons.data.models

data class HarvestRequest(
    val plantId: Long,
    val date: String,
    val quantity: Int,
    val description: String
)
