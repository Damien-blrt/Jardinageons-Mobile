package app.jardinageons.presentation.features.seedInventory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.models.Seed
import app.jardinageons.data.repositories.SeedRepository
import app.jardinageons.data.services.RetrofitClient.seedService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class SeedRequest(
    val name: String,
    val quantity: Int,
    val germinationTime: Int,
    val description: String,
    val vegetableId: Int,
    val expiryDate: String
)

enum class Event {
    modifiedSuccess,
    modifiedError,
    addSuccess,
    addError,
    deleteSuccess,
    deleteError,
}

class SeedInventoryViewModel(private val _repository: SeedRepository = SeedRepository(seedService)) :
    ViewModel() {

    private val _seeds = MutableStateFlow<List<Seed>>(emptyList())

    val seeds: StateFlow<List<Seed>> = _seeds.asStateFlow()

    private val _totalSeeds = MutableStateFlow(0)
    val totalSeeds: StateFlow<Int> = _totalSeeds.asStateFlow()

    private val _averageGerminationTime = MutableStateFlow(0)
    val averageGerminationTime: StateFlow<Int> = _averageGerminationTime.asStateFlow()

    // Flow pour les événements UI (Snackbar et plus si besoin)
    // source : https://bytegoblin.io/blog/how-to-handle-single-event-in-jetpack-compose.mdx
    private val _uiEvent = MutableSharedFlow<Event>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadSeeds()
    }

    private fun loadSeeds() {
        viewModelScope.launch {
            try {
                val response = _repository.getSeeds(0, 10)
                normalizeSeeds(response)
                getTotalSeeds(response)
                getAverageGerminationTime(response)
            } catch (e: Exception) {
                Log.e("SeedInventoryViewModel", "Error loading seeds", e)
            } finally {
                Log.d("SeedInventoryViewModel", "Loading seeds completed")
            }
        }
    }

    fun createSeed(seed: SeedRequest) {
        Log.d("SeedInventoryViewModel", "Creating seed: $seed")
        viewModelScope.launch {
            try {
                _repository.createSeed(seed)
                loadSeeds()
                _uiEvent.emit(Event.addSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(Event.addError)
                Log.e("SeedInventoryViewModel", "Error creating seed.", e)
            } finally {
                Log.d("SeedInventoryViewModel", "Seed Created.")
            }
        }
    }

    private fun normalizeSeeds(response: PagedResponse<Seed>) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val cleanList = response.items.map { seed ->
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
        _seeds.value = cleanList
    }

    private fun getTotalSeeds(response: PagedResponse<Seed>) {
        _totalSeeds.value = response.items.sumOf { it.quantity }
    }

    private fun getAverageGerminationTime(response: PagedResponse<Seed>) {
        if (response.items.isNotEmpty()) {
            val total = response.items.sumOf { it.germinationTime }
            _averageGerminationTime.value = total / response.items.size
        }
    }

    fun deleteSeed(id: Long) {
        viewModelScope.launch {
            try {
                _repository.deleteSeed(id)
                loadSeeds()
                _uiEvent.emit(Event.deleteSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(Event.deleteError)
                Log.e("SeedInventoryViewModel", "Error deleting seed.", e)
            }
        }
    }

    fun updateSeed(id: Long, seed: Seed) {
        viewModelScope.launch {
            try {
                _repository.updateSeed(id, seed)
                loadSeeds()
                _uiEvent.emit(Event.modifiedSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(Event.modifiedError)
                Log.e("SeedInventoryViewModel", "Error updating seed.", e)
            }
        }
    }

}