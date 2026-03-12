package app.jardinageons.data.models

import com.google.gson.annotations.SerializedName

data class Garden(
    val id: Long,
    val name: String,
    @SerializedName(
        value = "canvas_JSON",
        alternate = ["canvas_json", "canvaJson", "canva_json"]
    )
    val canvasJson: String? = null
)
