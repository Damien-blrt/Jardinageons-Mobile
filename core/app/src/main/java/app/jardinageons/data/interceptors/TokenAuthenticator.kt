package app.jardinageons.data.interceptors

import app.jardinageons.data.models.RefreshRequest
import app.jardinageons.data.storage.TokenManager
import app.jardinageons.data.services.RetrofitClient
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * OkHttp Authenticator qui intercepte les réponses 401 (Unauthorized)
 * et tente de rafraîchir le token automatiquement.
 */
class TokenAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        val refreshToken = TokenManager.refreshToken ?: return null

        // Appel synchrone pour rafraîchir le token
        val refreshResponse = try {
            RetrofitClient.loginQService.refreshToken(
                RefreshRequest(refreshToken = refreshToken)
            ).execute()
        } catch (e: Exception) {
            return null
        }

        return if (refreshResponse.isSuccessful) {
            val newTokens = refreshResponse.body() ?: return null

            // Mettre à jour les tokens en mémoire
            TokenManager.accessToken = newTokens.accessToken
            TokenManager.refreshToken = newTokens.refreshToken

            // Relancer la requête originale avec le nouveau token
            response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokens.accessToken}")
                .build()
        } else {
            null
        }
    }

    /**
     * Compte le nombre de tentatives pour éviter les boucles infinies
     */
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
