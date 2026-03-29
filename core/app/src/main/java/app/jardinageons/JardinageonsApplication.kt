package app.jardinageons

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import app.jardinageons.data.database.JardinageonsDatabase
import app.jardinageons.data.workers.WateringWorker

class JardinageonsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        JardinageonsDatabase.initialize(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = WateringWorker.CHANNEL_NAME
            val descriptionText = "Notifications pour les rappels d'arrosage"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(WateringWorker.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}