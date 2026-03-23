package app.jardinageons.data.repositories
import android.util.Log
import app.jardinageons.data.dao.SeedDao
import app.jardinageons.data.entities.SeedEntity
import app.jardinageons.data.models.Seed
import app.jardinageons.data.services.ISeedService
import app.jardinageons.presentation.features.seedInventory.SeedRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


class SeedRepository(private val _service: ISeedService,
    private  val seedDao: SeedDao) {
    private val pageIndex = 0;
    private val countPerPage = 10;


    fun getSeedsFlow(): Flow<List<Seed>> {
        return seedDao.loadSeeds().map { entities ->
            entities.map { entity ->
                Seed(
                    id = entity.id,
                    name = entity.name,
                    quantity = entity.quantity,
                    germinationTime = entity.germinationTime,
                    description = entity.description,
                    vegetableId = entity.vegetableId,
                    expiryDate = entity.expiryDate
                )
            }
        }
    }
    suspend fun refreshSeeds(pageIndex: Int = this.pageIndex, countPerPage: Int = this.countPerPage) {
            try {
                val response = _service.listSeeds(pageIndex, countPerPage)

                val entities = response.items.map {
                    SeedEntity(
                        id = it.id,
                        name = it.name,
                        quantity = it.quantity,
                        germinationTime = it.germinationTime,
                        description = it.description,
                        vegetableId = it.vegetableId,
                        expiryDate = it.expiryDate
                    )
                }
                seedDao.insertSeeds(entities)
                Log.i("Repository", "Seeds refresh")
            } catch (e: Exception) {
                throw e
            }

    }
    suspend fun createSeed(seed: SeedRequest) {
        return withContext(Dispatchers.IO) {
            _service.createSeed(seed)
            refreshSeeds(pageIndex, countPerPage)
        }
    }

    suspend fun deleteSeed(id: Long) {
        return withContext(Dispatchers.IO) {
            _service.deleteSeed(id)
            refreshSeeds(pageIndex, countPerPage)
        }
    }

    suspend fun updateSeed(id: Long, seed: Seed) {
        return withContext(Dispatchers.IO) {
            _service.updateSeed(seed.id, seed)
            refreshSeeds(pageIndex, countPerPage)
        }
    }
}