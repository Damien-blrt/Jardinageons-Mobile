package app.jardinageons.data.models

import com.google.gson.annotations.SerializedName

data class VegetableRequest(
    val name: String,
    @SerializedName("germinationTime")
    val germinationTime: Int,
    val description: String,
    @SerializedName("sowingStart")
    val sowingStart: String,
    @SerializedName("sowingEnd")
    val sowingEnd: String,
    @SerializedName("harvestStart")
    val harvestStart: String,
    @SerializedName("harvestEnd")
    val harvestEnd: String
)
