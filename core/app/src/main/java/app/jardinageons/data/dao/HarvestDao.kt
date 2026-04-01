package app.jardinageons.data.dao;

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.jardinageons.data.entities.HarvestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HarvestDao{
    @Query("SELECT * FROM Harvest")
    fun loadHarvests(): Flow<List<HarvestEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHarvest(harvest: HarvestEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHarvests(harvests: List<HarvestEntity>)
    @Update
    suspend fun updateHarvest(harvests: HarvestEntity)
    @Delete
    suspend fun deleteHarvest(harvests: HarvestEntity)
    @Query("DELETE FROM Harvest")
    suspend fun clearHarvests()
}