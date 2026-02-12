package app.jardinageons.data.models
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Seed(
    val id: Long,
    val name: String,
    val quantity: Float,
    val germinationTime: Int,
    val description: String,
    val vegetableId: Long?,
    val expiryDate: String
)
