package app.jardinageons.presentation.features.statPage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jardinageons.data.models.Seed
import app.jardinageons.data.repositories.SeedRepository
import app.jardinageons.data.services.RetrofitClient.seedService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatViewModel(
    private val seedRepository: SeedRepository = SeedRepository(seedService),
    // private val vegetableRepository: VegetableRepository = VegetableRepository(vegetableService)
) : ViewModel() {

    private val _seeds = MutableStateFlow<List<Seed>>(emptyList())
    val seeds: StateFlow<List<Seed>> = _seeds.asStateFlow()

    private val _totalSeeds = MutableStateFlow(0)
    val totalSeeds: StateFlow<Int> = _totalSeeds.asStateFlow()

    // private val _vegetables = MutableStateFlow<List<Vegetable>>(emptyList())
    // val vegetables: StateFlow<List<Vegetable>> = _vegetables.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val seedResponse = seedRepository.getSeeds(0, 100)
                Log.d("StatViewModel", "Seeds chargées: ${seedResponse.items.size}") // <-- ajoute
                _seeds.value = seedResponse.items
                _totalSeeds.value = seedResponse.items.sumOf { it.quantity }
            } catch (e: Exception) {
                Log.e("StatViewModel", "Erreur: ${e.message}", e) // <-- ajoute
                _seeds.value = emptyList()
                _totalSeeds.value = 0
            } finally {
                _isLoading.value = false
            }
        }
    }
}