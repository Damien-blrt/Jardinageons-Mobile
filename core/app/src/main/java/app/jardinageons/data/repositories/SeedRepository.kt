package app.jardinageons.data.repositories

import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.models.Seed
import app.jardinageons.data.services.ISeedService
import app.jardinageons.presentation.features.seedInventory.SeedRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SeedRepository(private val _service: ISeedService) {
    suspend fun getSeeds(pageIndex: Int, countPerPage: Int): PagedResponse<Seed> {
        return withContext(Dispatchers.IO) {
            _service.listSeeds(pageIndex, countPerPage)
        }
    }

    suspend fun createSeed(seed: SeedRequest) {
        return withContext(Dispatchers.IO) {
            _service.createSeed(seed)
        }
    }

    suspend fun deleteSeed(id: Long) {
        return withContext(Dispatchers.IO) {
            _service.deleteSeed(id)
        }
    }

    suspend fun updateSeed(id: Long, seed: Seed) {
        return withContext(Dispatchers.IO) {
            _service.updateSeed(seed.id, seed)
        }
    }
}