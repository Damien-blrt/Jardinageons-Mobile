package app.jardinageons.data.services

import app.jardinageons.data.annotations.InjectAuth
import app.jardinageons.data.models.Grow
import app.jardinageons.data.models.PagedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface IGrowService {
    @InjectAuth
    @GET("api/v1/grow")
    suspend fun listGrows(
        @Query("pageIndex") pageIndex: Int,
        @Query("countPerPage") countPerPage: Int
    ): PagedResponse<Grow>
}
