package app.jardinageons.data.services

import app.jardinageons.data.annotations.InjectAuth
import app.jardinageons.data.models.Harvest
import app.jardinageons.data.models.HarvestRequest
import app.jardinageons.data.models.PagedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface HarvestService {
    @InjectAuth
    @GET("api/v1/Harvest")
    suspend fun listHarvests(
        @Query("pageIndex") pageIndex: Int,
        @Query("countPerPage") countPerPage: Int
    ): PagedResponse<Harvest>

    @InjectAuth
    @POST("api/v1/Harvest")
    suspend fun createHarvest(@Body harvest: HarvestRequest): Harvest

    @InjectAuth
    @DELETE("api/v1/Harvest/{id}")
    suspend fun deleteHarvest(@Path("id") id: Long): Response<Unit>

    @InjectAuth
    @PUT("api/v1/Harvest/{id}")
    suspend fun updateHarvest(@Path("id") id: Long, @Body harvest: Harvest): Harvest
}
