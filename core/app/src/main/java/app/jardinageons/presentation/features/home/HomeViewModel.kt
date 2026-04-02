package app.jardinageons.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jardinageons.data.models.Advice
import app.jardinageons.data.repositories.AdviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel : ViewModel() {
    private val repository = AdviceRepository()

    private val _advices = MutableStateFlow<List<Advice>>(emptyList())

    private val _filteredAdvices = MutableStateFlow<List<Advice>>(emptyList())
    val filteredAdvices: StateFlow<List<Advice>> = _filteredAdvices.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val frenchMonths = listOf(
        "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    )

    val currentMonthName: String = frenchMonths[Calendar.getInstance().get(Calendar.MONTH)]

    init {
        loadAdvices()
    }

    private fun loadAdvices() {
        viewModelScope.launch {
            _isLoading.value = true
            val fetchedAdvices = repository.getAdvices()
            if (fetchedAdvices != null) {
                _advices.value = fetchedAdvices
                _filteredAdvices.value = filterForCurrentMonth(fetchedAdvices)
            }
            _isLoading.value = false
        }
    }

    private fun filterForCurrentMonth(advices: List<Advice>): List<Advice> {
        val normalizedCurrentMonth = currentMonthName.lowercase()
            .replace("é", "e")
            .replace("û", "u")

        return advices.filter { advice ->
            val normalizedApiMonth = advice.month
                ?.lowercase()
                ?.replace("é", "e")
                ?.replace("û", "u")
            normalizedApiMonth == normalizedCurrentMonth
        }.take(2)
    }
}
