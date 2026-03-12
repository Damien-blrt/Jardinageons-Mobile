package app.jardinageons.presentation.features.vegetable


import app.jardinageons.data.repositories.VegetableRepository
import app.jardinageons.data.services.RetrofitClient.vegetableService
import java.time.LocalDate

data class VegetableRequest(
    val name: String,
    val germinationTime: Int,
    val description: String,
    val sowingStart: LocalDate,
    val sowingEnd: LocalDate,
    val harvestStart: LocalDate,
    val harvestEnd: LocalDate,
    val waterNeedsMm: Double? = 5.0
)

enum class Event {
    modifiedSuccess,
    modifiedError,
    addSuccess,
    addError,
    deleteSuccess,
    deleteError,
}


class VegetableViewModel(
    private val _repository: VegetableRepository = VegetableRepository(
        vegetableService
    )
) {

}
