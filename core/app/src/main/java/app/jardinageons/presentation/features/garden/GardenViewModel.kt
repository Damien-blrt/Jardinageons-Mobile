package app.jardinageons.presentation.features.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jardinageons.data.models.Garden
import app.jardinageons.data.repositories.GardenRepository
import app.jardinageons.data.services.RetrofitClient
import app.jardinageons.presentation.features.garden.model.GardenCanvasModel
import app.jardinageons.presentation.features.garden.model.parseGardenCanvasModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GardenUiState(
    val isLoading: Boolean = true,
    val gardens: List<Garden> = emptyList(),
    val selectedGardenId: Long? = null,
    val selectedCanvas: GardenCanvasModel? = null,
    val errorMessage: String? = null
)

class GardenViewModel(
    private val repository: GardenRepository = GardenRepository(RetrofitClient.gardenService)
) : ViewModel() {

    private val _uiState = MutableStateFlow(GardenUiState())
    val uiState: StateFlow<GardenUiState> = _uiState.asStateFlow()

    init {
        loadGardens()
    }

    fun loadGardens() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                repository.getGardens(pageIndex = 0, countPerPage = 100).items
            }.onSuccess { gardens ->
                val initialGarden = gardens.firstOrNull()
                _uiState.value = GardenUiState(
                    isLoading = false,
                    gardens = gardens,
                    selectedGardenId = initialGarden?.id,
                    selectedCanvas = parseGardenCanvasModel(initialGarden?.canvasJson),
                    errorMessage = null
                )
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Impossible de charger le jardin"
                    )
                }
            }
        }
    }

    fun selectGarden(gardenId: Long) {
        val state = _uiState.value
        if (state.selectedGardenId == gardenId) return

        val selectedGarden = state.gardens.firstOrNull { it.id == gardenId }
        _uiState.update {
            it.copy(
                selectedGardenId = gardenId,
                selectedCanvas = parseGardenCanvasModel(selectedGarden?.canvasJson),
                errorMessage = null
            )
        }
    }
}
