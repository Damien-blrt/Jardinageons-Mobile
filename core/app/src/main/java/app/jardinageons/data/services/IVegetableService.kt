package app.jardinageons.data.services

import app.jardinageons.data.annotations.InjectAuth
import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.models.Vegetable
import app.jardinageons.presentation.features.vegetable.VegetableRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface IVegetableService {
    @InjectAuth
    @GET("api/v1/Vegetable")
    suspend fun listVegetables(
        @Query("pageIndex") pageIndex: Int,
        @Query("countPerPage") countPerPage: Int
    ): PagedResponse<Vegetable>

    @InjectAuth
    @POST("api/v1/Vegetable")
    suspend fun createVegetable(@Body vegetable: VegetableRequest): Vegetable

    @InjectAuth
    @DELETE("api/v1/Vegetable/{id}")
    suspend fun deleteVegetable(@Path("id") id: Long): Response<Unit>

    @InjectAuth
    @PUT("api/v1/Vegetable/{id}")
    suspend fun updateVegetable(@Path("id") id: Long, @Body vegetable: Vegetable): Vegetable
}