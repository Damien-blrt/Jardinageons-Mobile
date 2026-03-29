package app.jardinageons.data.repositories

import app.jardinageons.data.models.Grow
import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.services.IGrowService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GrowsRepository(private val _service: IGrowService) {
    suspend fun getGrows(pageIndex: Int, countPerPage: Int): PagedResponse<Grow> {
        return withContext(Dispatchers.IO) {
            _service.listGrows(pageIndex, countPerPage)
        }
    }
}
