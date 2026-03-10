package app.jardinageons.data.models

data class Tokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long
)