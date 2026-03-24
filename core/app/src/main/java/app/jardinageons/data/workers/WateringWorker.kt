package app.jardinageons.data.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.jardinageons.R
import app.jardinageons.data.repositories.GrowsRepository
import app.jardinageons.data.repositories.VegetableRepository
import app.jardinageons.data.services.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WateringWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val growsRepository = GrowsRepository(RetrofitClient.growService)
    private val vegetableRepository = VegetableRepository(RetrofitClient.vegetableService)

    companion object {
        const val CHANNEL_ID = "watering_reminders"
        const val CHANNEL_NAME = "Rappels d'arrosage"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Récupérer la liste des plantes en cours de croissance
            val pagedResponse = growsRepository.getGrows(pageIndex = 0, countPerPage = 50)
            val grows = pagedResponse.items

            if (grows.isNotEmpty()) {
                val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                grows.forEachIndexed { index, grow ->
                    try {
                        // Récupérer les détails du légume via son ID
                        val vegetable = vegetableRepository.getVegetableById(grow.vegetableId)
                        
                        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                            .setSmallIcon(R.drawable.potted_plant)
                            .setContentTitle("Rappel d'arrosage")
                            .setContentText("Pensez à arroser votre ${vegetable.name}")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)
                            .build()

                        notificationManager.notify(index + 200, notification)
                    } catch (e: Exception) {
                        // Ignorer si un légume spécifique ne peut pas être récupéré
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
