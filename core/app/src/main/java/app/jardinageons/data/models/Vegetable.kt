package app.jardinageons.data.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Vegetable (
    val id: Long,
    val name: String,
    val germinationTime : Int,
    val description : String,
    val sowingStart : LocalDate,
    val sowingEnd : LocalDate,
    val harvestStart : LocalDate,
    val harvestEnd : LocalDate,
    @SerializedName("water_needs_mm")
    val waterNeedsMm: Double? = 5.0
    )