package app.jardinageons.presentation.features.vegetable.components

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jardinageons.BuildConfig
import app.jardinageons.data.models.Vegetable
import app.jardinageons.data.repositories.WeatherRepository
import app.jardinageons.data.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VegetableViewModel : ViewModel() {

    private val vegetableService = RetrofitClient.vegetableService
    private val weatherRepository = WeatherRepository()

    private val _vegetables = MutableStateFlow<List<Vegetable>>(emptyList())
    val vegetables: StateFlow<List<Vegetable>> = _vegetables.asStateFlow()

    private val _rainForecast = MutableStateFlow<Double?>(null)
    val rainForecast: StateFlow<Double?> = _rainForecast.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val summary = weatherRepository.getWeatherSummary(-3.11, -60.02)
                _rainForecast.value = summary?.rainTotal24h
                val response = vegetableService.listVegetables(0, 50)
                _vegetables.value = response.items

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}