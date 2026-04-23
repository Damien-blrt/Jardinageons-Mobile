package app.jardinageons.data.services

import app.jardinageons.data.models.Advice
import app.jardinageons.data.models.PagedResponse
import retrofit2.Response
import retrofit2.http.GET

interface IAdviceService {
    @GET("api/v1/Advice")
    suspend fun getAdvices(): Response<PagedResponse<Advice>>
}