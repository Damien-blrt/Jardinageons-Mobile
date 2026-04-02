package app.jardinageons.presentation.features.vegetable

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jardinageons.data.models.Vegetable
import app.jardinageons.data.models.VegetableRequest
import app.jardinageons.data.repositories.VegetableRepository
import app.jardinageons.data.services.RetrofitClient.vegetableService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

enum class Event {
    addSuccess, addError, modifiedSuccess, modifiedError, deleteSuccess, deleteError
}

class VegetableViewModel(
    private val _repository: VegetableRepository = VegetableRepository(vegetableService)
) : ViewModel() {

    private val _vegetables = MutableStateFlow<List<Vegetable>>(emptyList())
    val vegetables: StateFlow<List<Vegetable>> = _vegetables.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredVegetables: StateFlow<List<Vegetable>> = combine(_vegetables, _searchQuery) { vegetables, query ->
        if (query.isBlank()) vegetables
        else vegetables.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _totalVegetables = MutableStateFlow(0)
    val totalVegetables: StateFlow<Int> = _totalVegetables.asStateFlow()

    private val _uiEvent = MutableSharedFlow<Event>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadVegetables()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadVegetables() {
        viewModelScope.launch {
            try {
                val response = _repository.getVegetables(0, 10)
                normalizeVegetables(response.items)
                _totalVegetables.value = response.items.size
            } catch (e: Exception) {
                _vegetables.value = emptyList()
                _totalVegetables.value = 0
                Log.e("VegetableViewModel", "Error loading", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun formatDate(dateStr: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return try {
            ZonedDateTime.parse(dateStr).format(formatter)
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun normalizeVegetables(items: List<Vegetable>) {
        _vegetables.value = items.map { vegetable ->
            vegetable.copy(
                name = vegetable.name.uppercase(),
                sowingStart = formatDate(vegetable.sowingStart),
                sowingEnd = formatDate(vegetable.sowingEnd),
                harvestStart = formatDate(vegetable.harvestStart),
                harvestEnd = formatDate(vegetable.harvestEnd)
            )
        }
    }

    fun createVegetable(request: VegetableRequest) {
        viewModelScope.launch {
            try {
                _repository.createVegetable(request)
                loadVegetables()
                _uiEvent.emit(Event.addSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(Event.addError)
            }
        }
    }

    fun deleteVegetable(id: Long) {
        viewModelScope.launch {
            try {
                _repository.deleteVegetable(id)
                loadVegetables()
                _uiEvent.emit(Event.deleteSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(Event.deleteError)
            }
        }
    }

    fun updateVegetable(id: Long, vegetable: Vegetable) {
        viewModelScope.launch {
            try {
                _repository.updateVegetable(id, vegetable)
                loadVegetables()
                _uiEvent.emit(Event.modifiedSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(Event.modifiedError)
            }
        }
    }
}
