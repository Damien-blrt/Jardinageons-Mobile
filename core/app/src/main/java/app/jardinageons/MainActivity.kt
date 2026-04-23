package app.jardinageons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import app.jardinageons.data.storage.TokenManager
import app.jardinageons.data.storage.tokenDataStore
import app.jardinageons.presentation.components.AppNavGraph
import app.jardinageons.presentation.components.AuthNavGraph
import app.jardinageons.presentation.components.AuthorizeView
import app.jardinageons.presentation.theme.JardinageonsTheme
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import app.jardinageons.data.workers.SyncWorker
import app.jardinageons.data.workers.WateringWorker
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS).build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "syncTokens",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )

        WateringWorker.enqueue(applicationContext)

        lifecycleScope.launch {
            tokenDataStore.data.collect { tokens ->
                TokenManager.accessToken = tokens?.accessToken
                TokenManager.refreshToken = tokens?.refreshToken
            }
        }

        setContent {
            JardinageonsTheme {
                AuthorizeView(
                    authorized = {
                        AppNavGraph()
                    },
                    unauthorized = {
                        AuthNavGraph()
                    }
                )
            }
        }
    }
}
