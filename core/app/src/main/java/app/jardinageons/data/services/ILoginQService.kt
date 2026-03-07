package app.jardinageons.data.services

import app.jardinageons.data.models.LoginRequest
import app.jardinageons.data.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ILoginQService {
    @POST("authentication/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("authentication/register")
    suspend fun register(@Body request: LoginRequest): Response<Unit>
}
