package app.jardinageons.data.services

import app.jardinageons.data.annotations.InjectAuth
import app.jardinageons.data.models.Garden
import app.jardinageons.data.models.PagedResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IGardenService {
    @InjectAuth
    @GET("v1/Garden")
    suspend fun listGardens(
        @Query("pageIndex") pageIndex: Int,
        @Query("countPerPage") countPerPage: Int
    ): PagedResponse<Garden>

    @InjectAuth
    @GET("v1/Garden/{id}")
    suspend fun getGarden(@Path("id") id: Long): Garden
}
