package app.jardinageons.data.services

import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.models.Vegetable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IVegetableService {
    @GET("api/v1/Vegetable")
    fun listVegetables(
        @Query("pageIndex") pageIndex: Int,
        @Query("countPerPage") countPerPage: Int
    ): Call<PagedResponse<Vegetable>>
}