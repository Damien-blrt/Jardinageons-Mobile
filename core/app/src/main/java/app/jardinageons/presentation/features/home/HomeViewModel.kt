package app.jardinageons.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jardinageons.data.models.Advice
import app.jardinageons.data.repositories.AdviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = AdviceRepository()

    private val _advices = MutableStateFlow<List<Advice>>(emptyList())
    val advices: StateFlow<List<Advice>> = _advices.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAdvices()
    }

    private fun loadAdvices() {
        viewModelScope.launch {
            _isLoading.value = true
            val fetchedAdvices = repository.getAdvices()
            if (fetchedAdvices != null) {
                _advices.value = fetchedAdvices
            }
            _isLoading.value = false
        }
    }
}