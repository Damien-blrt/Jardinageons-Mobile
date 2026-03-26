package app.jardinageons.data.workers

import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import app.jardinageons.R
import app.jardinageons.data.repositories.GrowsRepository
import app.jardinageons.data.repositories.VegetableRepository
import app.jardinageons.data.services.RetrofitClient
import app.jardinageons.data.storage.TokenManager
import app.jardinageons.data.storage.tokenDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class WateringWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    companion object {
        const val CHANNEL_ID = "watering_reminders"
        const val CHANNEL_NAME = "Rappels d'arrosage"
        const val WORK_NAME = "wateringReminders"
        private const val TAG = "WateringWorker"
        private const val REPEAT_INTERVAL_DAYS = 3L

        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<WateringWorker>()
                .setInitialDelay(REPEAT_INTERVAL_DAYS, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueue(request)
            Log.d(TAG, "Worker planifié dans $REPEAT_INTERVAL_DAYS jours")
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val tokens = applicationContext.tokenDataStore.data.first()
            if (tokens?.accessToken == null) {
                Log.w(TAG, "Pas de token disponible, abandon.")
                scheduleNext()
                return@withContext Result.failure()
            }
            TokenManager.accessToken = tokens.accessToken
            TokenManager.refreshToken = tokens.refreshToken

            val growsRepository = GrowsRepository(RetrofitClient.growService)
            val vegetableRepository = VegetableRepository(RetrofitClient.vegetableService)

            val pagedResponse = growsRepository.getGrows(pageIndex = 0, countPerPage = 50)
            val grows = pagedResponse.items

            Log.d(TAG, "Plantes trouvées: ${grows.size}")

            if (grows.isNotEmpty()) {
                val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                grows.forEachIndexed { index, grow ->
                    try {
                        val vegetable = vegetableRepository.getVegetableById(grow.vegetableId)
                        val largeIcon = BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.ic_launcher)
                        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                            .setLargeIcon(largeIcon)
                            .setSmallIcon(R.drawable.potted_plant)
                            .setContentTitle("Rappel d'arrosage")
                            .setContentText("Pensez à arroser votre ${vegetable.name}")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)
                            .build()

                        notificationManager.notify(index + 200, notification)
                    } catch (e: Exception) {
                    }
                }
            }

            scheduleNext()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Erreur WateringWorker: ${e.message}", e)
            scheduleNext() // Re-planifier même en cas d'erreur
            Result.retry()
        }
    }

    /** Re-planifie le worker dans 3 jours */
    private fun scheduleNext() {
        val request = OneTimeWorkRequestBuilder<WateringWorker>()
            .setInitialDelay(REPEAT_INTERVAL_DAYS, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(request)
        Log.d(TAG, "Prochain worker planifié dans $REPEAT_INTERVAL_DAYS secondes")
    }
}
