package app.jardinageons.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.jardinageons.data.entities.SeedEntity
import kotlinx.coroutines.flow.Flow



@Dao
interface SeedDao{
    @Query("SELECT * FROM Seed")
    fun loadSeeds(): Flow<List<SeedEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeed(seed: SeedEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeeds(seeds: List<SeedEntity>)
    @Update
    suspend fun updateSeed(seed: SeedEntity)
    @Delete
    suspend fun deleteSeed(seed: SeedEntity)
    @Query("DELETE FROM Seed")
    suspend fun clearSeeds()
}

