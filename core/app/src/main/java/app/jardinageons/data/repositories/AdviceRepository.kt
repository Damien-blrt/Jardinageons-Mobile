package app.jardinageons.data.repositories

import android.util.Log
import app.jardinageons.data.models.Advice
import app.jardinageons.data.services.RetrofitClient

import app.jardinageons.data.services.IAdviceService

class AdviceRepository(private val api: IAdviceService = RetrofitClient.adviceService) {

    suspend fun getAdvices(): List<Advice>? {
        return try {
            val response = api.getAdvices()
            if (response.isSuccessful) {
                response.body()?.items
            } else {
                Log.e("AdviceRepository", "Erreur: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("AdviceRepository", "Exception: ${e.message}")
            null
        }
    }
}