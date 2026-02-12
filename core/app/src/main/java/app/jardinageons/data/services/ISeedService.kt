package app.jardinageons.data.services
import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.models.Seed
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
interface ISeedService {
    @GET("v1/Seed")
    suspend fun listSeeds(
        @Query("pageIndex") pageIndex: Int,
        @Query("countPerPage") countPerPage: Int
    ): Call<PagedResponse<Seed>>
}