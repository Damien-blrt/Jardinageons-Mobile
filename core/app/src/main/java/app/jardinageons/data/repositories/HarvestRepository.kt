package app.jardinageons.data.repositories;

import android.util.Log
import app.jardinageons.data.dao.HarvestDao
import app.jardinageons.data.entities.HarvestEntity
import app.jardinageons.data.models.Harvest
import app.jardinageons.data.services.HarvestService
import app.jardinageons.presentation.features.harvest.HarvestRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

public class HarvestRepository(private val _service: HarvestService,
    private val harvestDao: HarvestDao) {

    private val pageIndex = 0;
    private val countPerPage = 10;

    fun getHarvestsFlow(): Flow<List<Harvest>> {
        return harvestDao.loadHarvests().map { entities ->
            entities.map { entity ->
                Harvest(
                    id = entity.id,
                    plantId = entity.plantId,
                    quantity = entity.quantity,
                    description = entity.description,
                    date = entity.date,
                )
            }
        }
    }
    suspend fun refreshHarvests(pageIndex: Int = this.pageIndex, countPerPage: Int = this.countPerPage) {
        try {
            val response = _service.listHarvests(pageIndex, countPerPage)

            val entities = response.items.map {
                HarvestEntity(
                    id = it.id,
                    plantId = it.plantId,
                    quantity = it.quantity,
                    description = it.description,
                    date = it.date,
                )
            }
            harvestDao.insertHarvests(entities)
            Log.i("Repository", "Harvest refresh")
        } catch (e: Exception) {
            throw e
        }

    }
    suspend fun createHarvest(harvest: HarvestRequest) {
        return withContext(Dispatchers.IO) {
            _service.createHarvest(harvest)
            refreshHarvests(pageIndex, countPerPage)
        }
    }

    suspend fun deleteHarvest(id: Long) {
        return withContext(Dispatchers.IO) {
            _service.deleteHarvest(id)
            refreshHarvests(pageIndex, countPerPage)
        }
    }

    suspend fun updateHarvest(id: Long, harvest: Harvest) {
        return withContext(Dispatchers.IO) {
            _service.updateHarvest(harvest.id, harvest)
            refreshHarvests(pageIndex, countPerPage)
        }
    }
}
