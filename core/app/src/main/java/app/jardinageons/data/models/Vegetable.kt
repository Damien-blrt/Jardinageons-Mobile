package app.jardinageons.data.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Vegetable(
    val id: Long,
    val name: String,
    val germinationTime: Int,
    val description: String,
    val sowingStart: String,
    val sowingEnd: String,
    val harvestStart: String,
    val harvestEnd: String,
    @SerializedName("water_needs_mm")
    val waterNeedsMm: Double? = 5.0
)