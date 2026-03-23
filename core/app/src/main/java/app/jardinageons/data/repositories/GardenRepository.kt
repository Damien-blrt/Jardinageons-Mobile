package app.jardinageons.data.repositories

import app.jardinageons.data.models.Garden
import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.services.IGardenService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GardenRepository(
    private val service: IGardenService
) {
    suspend fun getGardens(pageIndex: Int, countPerPage: Int): PagedResponse<Garden> {
        return withContext(Dispatchers.IO) {
            service.listGardens(pageIndex, countPerPage)
        }
    }
}
