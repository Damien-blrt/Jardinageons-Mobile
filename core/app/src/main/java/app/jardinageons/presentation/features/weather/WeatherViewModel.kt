package app.jardinageons.presentation.features.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jardinageons.data.models.WeatherSummary
import app.jardinageons.data.repositories.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()

    private val _weatherSummary = MutableStateFlow<WeatherSummary?>(null)
    val weatherSummary: StateFlow<WeatherSummary?> = _weatherSummary.asStateFlow()


    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _weatherSummary.value = null

            val result = repository.getWeatherSummary(lat, lon)

            _weatherSummary.value = result
        }
    }
}