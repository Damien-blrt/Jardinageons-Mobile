package app.jardinageons.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(
    val totalCount: Int,
    val pageIndex: Int,
    val countPerPage: Int,
    val items: List<T>
)
