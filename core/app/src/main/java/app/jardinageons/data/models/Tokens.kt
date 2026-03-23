package app.jardinageons.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Tokens(
    val tokenType : String,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)