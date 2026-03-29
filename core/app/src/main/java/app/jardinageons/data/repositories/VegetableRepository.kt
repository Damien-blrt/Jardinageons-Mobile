package app.jardinageons.data.repositories

import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.models.Vegetable
import app.jardinageons.data.services.IVegetableService
import app.jardinageons.presentation.features.vegetable.VegetableRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VegetableRepository(private val _service: IVegetableService) {
    suspend fun getVegetables(pageIndex: Int, countPerPage: Int): PagedResponse<Vegetable> {
        return withContext(Dispatchers.IO) {
            _service.listVegetables(pageIndex, countPerPage)
        }
    }

    suspend fun createVegetable(vegetable: VegetableRequest) {
        return withContext(Dispatchers.IO) {
            _service.createVegetable(vegetable)
        }
    }

    suspend fun deleteVegetable(id: Long) {
        return withContext(Dispatchers.IO) {
            _service.deleteVegetable(id)
        }
    }

    suspend fun updateVegetable(id: Long, vegetable: Vegetable) {
        return withContext(Dispatchers.IO) {
            _service.updateVegetable(id, vegetable)
        }
    }

    suspend fun getVegetableById(id: Long): Vegetable {
        return withContext(Dispatchers.IO) {
            _service.getVegetableById(id)
        }
    }
}