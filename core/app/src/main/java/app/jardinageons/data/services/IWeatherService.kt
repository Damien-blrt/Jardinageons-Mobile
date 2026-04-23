package app.jardinageons.data.services

import app.jardinageons.data.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherService {

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}