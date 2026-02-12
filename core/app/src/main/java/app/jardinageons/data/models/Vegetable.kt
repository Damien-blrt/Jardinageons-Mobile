package app.jardinageons.data.models

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Vegetable (
    val id: Long,
    val name: String,
    val germinationTime : Int,
    val description : Int,
    val sowingStart : LocalDate,
    val sowingEnd : LocalDate,
    val harvestStart : LocalDate,
    val harvestEnd : LocalDate
    )