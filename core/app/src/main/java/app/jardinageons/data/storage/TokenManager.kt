package app.jardinageons.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import app.jardinageons.data.models.AuthResponse
import app.jardinageons.data.models.Tokens

class TokenManager(context: Context) {
    private val preferences : SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveTokens(tokens: AuthResponse){
        preferences.edit {
            putString("accessToken", tokens.accessToken)
            putString("refreshToken", tokens.refreshToken)
            putLong("expiresAt", System.currentTimeMillis() + tokens.expiresIn * 1000L)
        }
    }

    fun getAccessToken(): String?{
        return preferences.getString("accessToken",null)
    }

    fun getRefreshToken(): String?{
        return preferences.getString("refreshToken",null)
    }

    fun clearTokens(){
        preferences.edit{clear()}
    }

    fun getTokens(): Tokens?{
        val access = getAccessToken()
        val refresh = getRefreshToken()
        val expiresAt = preferences.getLong("expiresAt",0L)

        if(access != null && refresh != null){
            return Tokens(access, refresh, expiresAt)
        }
        return null
    }
}