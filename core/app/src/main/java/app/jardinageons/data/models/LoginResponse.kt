package app.jardinageons.data.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("tokenType") val tokenType: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("expiresIn") val expiresIn: Long,
    @SerializedName("refreshToken") val refreshToken: String
)
