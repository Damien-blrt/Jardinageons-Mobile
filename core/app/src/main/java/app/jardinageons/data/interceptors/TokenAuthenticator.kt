package app.jardinageons.data.interceptors

import app.jardinageons.data.models.RefreshRequest
import app.jardinageons.data.storage.TokenManager
import app.jardinageons.data.services.RetrofitClient
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        val refreshToken = TokenManager.refreshToken ?: return null

        val refreshResponse = try {
            RetrofitClient.loginQService.refreshToken(
                RefreshRequest(refreshToken = refreshToken)
            ).execute()
        } catch (e: Exception) {
            return null
        }

        return if (refreshResponse.isSuccessful) {
            val newTokens = refreshResponse.body() ?: return null

            TokenManager.accessToken = newTokens.accessToken
            TokenManager.refreshToken = newTokens.refreshToken

            response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokens.accessToken}")
                .build()
        } else {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}
