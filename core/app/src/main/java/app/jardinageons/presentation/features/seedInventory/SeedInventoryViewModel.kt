package app.jardinageons.presentation.features.seedInventory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jardinageons.data.database.JardinageonsDatabase
import app.jardinageons.data.models.Seed
import app.jardinageons.data.models.SeedRequest
import app.jardinageons.data.repositories.SeedRepository
import app.jardinageons.data.services.RetrofitClient.seedService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

enum class Event {
    modifiedSuccess,
    modifiedError,
    addSuccess,
    addError,
    deleteSuccess,
    deleteError,
}

class SeedInventoryViewModel(private val _repository: SeedRepository = SeedRepository(seedService,
    JardinageonsDatabase.getInstance().seedDao()
)) : ViewModel() {

    private val _seeds = MutableStateFlow<List<Seed>>(emptyList())
    val seeds: StateFlow<List<Seed>> = _seeds.asStateFlow()

    private val _totalSeeds = MutableStateFlow(0)
    val totalSeeds: StateFlow<Int> = _totalSeeds.asStateFlow()
    private val _averageGerminationTime = MutableStateFlow(0)
    val averageGerminationTime: StateFlow<Int> = _averageGerminationTime.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // source : https://bytegoblin.io/blog/how-to-handle-single-event-in-jetpack-compose.mdx
    private val _uiEvent = MutableSharedFlow<Event>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        observeLocalSeeds()
        refreshFromNetwork()
    }

    private fun observeLocalSeeds() {
        viewModelScope.launch {
            _repository.getSeedsFlow().collect { localSeeds ->
                val normalized = normalizeSeeds(localSeeds)
                _seeds.value = normalized
                getTotalSeeds(normalized)
                getAverageGerminationTime(normalized)
                if (_isLoading.value) {
                    _isLoading.value = false
                }
                Log.i("SeedInventoryViewModel", "Données locales chargées : ${localSeeds.size} graines")
            }
        }
    }

    private fun refreshFromNetwork() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                _repository.refreshSeeds()
            } catch (e: Exception) {
                _seeds.value = emptyList()
                _totalSeeds.value = 0
                _averageGerminationTime.value = 0
                Log.e("SeedInventoryViewModel", "Error loading seeds", e)
            } finally {
                _isRefreshing.value = false
                _isLoading.value = false
                Log.d("SeedInventoryViewModel", "Loading seeds completed")
            }
        }
    }

    fun createSeed(seed: SeedRequest) {
        Log.i("SeedInventoryViewModel", "Creating seed: $seed")
        val isoSeed = seed.copy(expiryDate = toIsoDate(seed.expiryDate))
        viewModelScope.launch {
            try {
                _repository.createSeed(isoSeed)
                _uiEvent.emit(Event.addSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(Event.addError)
                Log.e("SeedInventoryViewModel", "Error creating seed.", e)
            } finally {
                Log.i("SeedInventoryViewModel", "Seed Created.")
            }
        }
    }

    fun deleteSeed(id: Long) {
        viewModelScope.launch {
            try {
                _repository.deleteSeed(id)
                _uiEvent.emit(Event.deleteSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(Event.deleteError)
                Log.e("SeedInventoryViewModel", "Error deleting seed.", e)
            }
        }
    }

    fun updateSeed(id: Long, seed: Seed) {
        val isoSeed = seed.copy(expiryDate = toIsoDate(seed.expiryDate))
        viewModelScope.launch {
            try {
                _repository.updateSeed(id, isoSeed)
                _uiEvent.emit(Event.modifiedSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(Event.modifiedError)
                Log.e("SeedInventoryViewModel", "Error updating seed.", e)
            }
        }
    }

    private fun toIsoDate(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            outputFormat.format(inputFormat.parse(dateStr) ?: Date())
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun normalizeSeeds(items: List<Seed>): List<Seed> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return items.map { seed ->
            /*
            doc: https://medium.com/@arshamjafari85/mastering-date-and-time-handling-in-kotlin-35cc1192d226
             */
            val formattedDate = try {
                val dateObj = ZonedDateTime.parse(seed.expiryDate)
                dateObj.format(formatter)
            } catch (e: Exception) {
                seed.expiryDate
            }
            seed.copy(
                name = seed.name.uppercase(),
                expiryDate = formattedDate
            )
        }
    }

    private fun getTotalSeeds(items: List<Seed>) {
        _totalSeeds.value = items.sumOf { it.quantity }
    }

    private fun getAverageGerminationTime(items: List<Seed>) {
        if (items.isNotEmpty()) {
            val total = items.sumOf { it.germinationTime }
            _averageGerminationTime.value = total / items.size
        }
    }
}
