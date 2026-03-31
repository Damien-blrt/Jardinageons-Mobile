package app.jardinageons.data.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(
    @SerializedName(value = "totalCount", alternate = ["TotalCount"])
    val totalCount: Int,
    @SerializedName(value = "pageIndex", alternate = ["PageIndex"])
    val pageIndex: Int,
    @SerializedName(value = "countPerPage", alternate = ["CountPerPage"])
    val countPerPage: Int,
    @SerializedName(value = "items", alternate = ["Items"])
    val items: List<T>
)
