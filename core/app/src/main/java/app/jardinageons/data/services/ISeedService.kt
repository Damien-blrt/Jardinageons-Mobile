package app.jardinageons.data.services

import app.jardinageons.data.annotations.InjectAuth
import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.models.Seed
import app.jardinageons.presentation.features.seedInventory.SeedRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ISeedService {
    @InjectAuth
    @GET("api/v1/Seed")
    suspend fun listSeeds(
        @Query("pageIndex") pageIndex: Int,
        @Query("countPerPage") countPerPage: Int
    ): PagedResponse<Seed>

    @InjectAuth
    @POST("api/v1/Seed")
    suspend fun createSeed(@Body seed: SeedRequest): Seed

    @InjectAuth
    @DELETE("api/v1/Seed/{id}")
    suspend fun deleteSeed(@Path("id") id: Long): Response<Unit>

    @InjectAuth
    @PUT("api/v1/Seed/{id}")
    suspend fun updateSeed(@Path("id") id: Long, @Body seed: Seed): Seed
}
