package app.jardinageons.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Seed")
data class SeedEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var name: String,
    var quantity: Int,
    var germinationTime: Int,
    var description: String,
    var vegetableId: Long?,
    var expiryDate: String
)
