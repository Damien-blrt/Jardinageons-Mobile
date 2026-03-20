package app.jardinageons.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Harvest")
data class HarvestEntity (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val plantId: Long,
    val date: String,
    val quantity: Int,
    val description: String
)