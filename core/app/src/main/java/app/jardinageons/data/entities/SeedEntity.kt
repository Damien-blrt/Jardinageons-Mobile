package app.jardinageons.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Seed")
data class SeedEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val quantity: Int,
    val germinationTime: Int,
    val description: String,
    val vegetableId: Long?,
    val expiryDate: String
)
