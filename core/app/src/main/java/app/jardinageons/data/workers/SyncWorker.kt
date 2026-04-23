package app.jardinageons.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.jardinageons.data.models.RefreshRequest
import app.jardinageons.data.models.Tokens
import app.jardinageons.data.services.RetrofitClient
import app.jardinageons.data.storage.TokenManager
import app.jardinageons.data.storage.tokenDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val refreshToken = TokenManager.refreshToken ?: return@withContext Result.failure()
            
            val response = RetrofitClient.loginQService.refreshToken(RefreshRequest(refreshToken)).execute()

            if (response.isSuccessful) {
                val newTokens = response.body()
                if (newTokens != null) {
                    TokenManager.accessToken = newTokens.accessToken
                    TokenManager.refreshToken = newTokens.refreshToken
                    
                    applicationContext.tokenDataStore.updateData {
                        Tokens(
                            tokenType = newTokens.tokenType,
                            accessToken = newTokens.accessToken,
                            expiresIn = newTokens.expiresIn,
                            refreshToken = newTokens.refreshToken
                        )
                    }
                    return@withContext Result.success()
                }
            }
            
            Result.retry()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
