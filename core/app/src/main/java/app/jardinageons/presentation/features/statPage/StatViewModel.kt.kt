package app.jardinageons.presentation.features.statPage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jardinageons.data.database.JardinageonsDatabase
import app.jardinageons.data.models.Seed
import app.jardinageons.data.repositories.SeedRepository
import app.jardinageons.data.services.RetrofitClient.seedService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatViewModel(
    private val seedRepository: SeedRepository = SeedRepository(
        seedService,
        JardinageonsDatabase.getInstance().seedDao()
    ),
) : ViewModel() {

    private val _seeds = MutableStateFlow<List<Seed>>(emptyList())
    val seeds: StateFlow<List<Seed>> = _seeds.asStateFlow()

    private val _totalSeeds = MutableStateFlow(0)
    val totalSeeds: StateFlow<Int> = _totalSeeds.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            observeLocalSeeds()
        }
        viewModelScope.launch(Dispatchers.IO) {
            refreshFromNetwork()
        }
    }

    private fun observeLocalSeeds() {
        viewModelScope.launch {
            seedRepository.getSeedsFlow().collect { seeds ->
                _seeds.value = seeds
                _totalSeeds.value = seeds.sumOf { it.quantity }
                if (_isLoading.value) {
                    _isLoading.value = false
                }
                Log.d("StatViewModel", "Seeds locales: ${seeds.size}")
            }
        }
    }

    private fun refreshFromNetwork() {
        viewModelScope.launch {
            try {
                seedRepository.refreshSeeds()
            } catch (e: Exception) {
                Log.e("StatViewModel", "Erreur réseau: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}