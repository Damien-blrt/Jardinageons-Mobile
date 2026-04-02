package app.jardinageons.presentation.features.harvest

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jardinageons.data.database.JardinageonsDatabase
import app.jardinageons.data.models.Harvest
import app.jardinageons.data.repositories.HarvestRepository
import app.jardinageons.data.services.RetrofitClient.harvestService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

enum class HarvestEvent {
    modifiedSuccess,
    modifiedError,
    deleteSuccess,
    deleteError
}

class HarvestViewModel(private val _repository: HarvestRepository = HarvestRepository(harvestService,
    JardinageonsDatabase.getInstance().harvestDao()
)) : ViewModel() {

    private val _harvests = MutableStateFlow<List<Harvest>>(emptyList())
    val harvests: StateFlow<List<Harvest>> = _harvests.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredHarvests: StateFlow<List<Harvest>> = combine(_harvests, _searchQuery) { harvests, query ->
        if (query.isBlank()) harvests
        else harvests.filter {
            it.description.contains(query, ignoreCase = true) ||
                    it.date.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _totalHarvests = MutableStateFlow(0)
    val totalHarvests: StateFlow<Int> = _totalHarvests.asStateFlow()

    private val _isFirstLoading = MutableStateFlow(true)
    val isFirstLoading: StateFlow<Boolean> = _isFirstLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HarvestEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        observeLocalHarvests()
        refreshFromNetwork()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun observeLocalHarvests() {
        viewModelScope.launch {
            _repository.getHarvestsFlow().collect { localHarvests ->
                val normalized = normalizeHarvests(localHarvests)
                _harvests.value = normalized
                getTotalHarvests(normalized)
                if (_isFirstLoading.value) {
                    _isFirstLoading.value = false
                }
                Log.i("HarvestViewModel", "Données locales chargées : ${localHarvests.size} récoltes")
            }
        }
    }

    private fun refreshFromNetwork() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                _repository.refreshHarvests()
            } catch (e: Exception) {
                _harvests.value = emptyList()
                _totalHarvests.value = 0
                Log.e("HarvestViewModel", "Error loading harvests", e)
            } finally {
                _isRefreshing.value = false
                _isFirstLoading.value = false
                Log.d("HarvestViewModel", "Loading harvests completed")
            }
        }
    }

    fun deleteHarvest(id: Long) {
        viewModelScope.launch {
            try {
                _repository.deleteHarvest(id)
                _uiEvent.emit(HarvestEvent.deleteSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(HarvestEvent.deleteError)
                Log.e("HarvestViewModel", "Error deleting Harvest.", e)
            }
        }
    }

    fun updateHarvest(id: Long, harvest: Harvest) {
        val isoHarvest = harvest.copy(date = toIsoDate(harvest.date))
        viewModelScope.launch {
            try {
                _repository.updateHarvest(id, isoHarvest)
                _uiEvent.emit(HarvestEvent.modifiedSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(HarvestEvent.modifiedError)
                Log.e("HarvestViewModel", "Error updating Harvest.", e)
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

    private fun normalizeHarvests(items: List<Harvest>): List<Harvest> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return items.map { harvest ->
            val formattedDate = try {
                val dateObj = ZonedDateTime.parse(harvest.date)
                dateObj.format(formatter)
            } catch (e: Exception) {
                harvest.date
            }
            harvest.copy(
                date = formattedDate,
                description = harvest.description.trim()
            )
        }
    }

    private fun getTotalHarvests(items: List<Harvest>) {
        _totalHarvests.value = items.sumOf { it.quantity }
    }
}
