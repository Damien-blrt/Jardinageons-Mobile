package app.jardinageons.data.repositories

import app.jardinageons.BuildConfig
import app.jardinageons.data.models.WeatherSummary
import app.jardinageons.data.services.RetrofitClient
import android.util.Log

class WeatherRepository {

    private val weatherService = RetrofitClient.weatherService

    /**
     * Récupère le résumé météo (pluie 24h et température actuelle).
     * Renvoie null en cas d'erreur.
     */
    suspend fun getWeatherSummary(lat: Double, lon: Double): WeatherSummary? {
        return try {
            val response = weatherService.getForecast(
                lat = lat,
                lon = lon,
                apiKey = BuildConfig.WEATHER_API_KEY
            )

            var totalRain = 0.0
            response.list.take(8).forEach { forecast ->
                totalRain += forecast.rain?.threeHour ?: 0.0
            }

            val currentTemp = response.list.firstOrNull()?.main?.temp
            val currentWindMs = response.list.firstOrNull()?.wind?.speed
            val currentWindKmh = currentWindMs?.let { it * 3.6}

            val cityName = response.city?.name ?: "Mon Jardin"

            WeatherSummary(
                rainTotal24h = totalRain,
                currentTemp = currentTemp,
                locationName = cityName,
                humidity = response.list.firstOrNull()?.main?.humidity,
                windSpeedKmh = currentWindKmh
            )

        } catch (e: Exception) {
            Log.e("WeatherRepository", "Erreur lors de la récupération de la météo", e)
            null
        }
    }
}