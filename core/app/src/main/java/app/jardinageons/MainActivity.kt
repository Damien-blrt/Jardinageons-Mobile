package app.jardinageons

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.core.MultiProcessDataStoreFactory
import androidx.lifecycle.lifecycleScope
import app.jardinageons.data.models.LoginRequest
import app.jardinageons.data.services.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.jardinageons.data.models.Tokens
import app.jardinageons.data.storage.TokenManager
import app.jardinageons.data.storage.TokenSerializer
import app.jardinageons.data.storage.tokenDataStore
import app.jardinageons.presentation.features.home.HomeScreen
import app.jardinageons.presentation.features.login.LoginScreen
import app.jardinageons.presentation.features.register.RegisterScreen
import app.jardinageons.presentation.components.AppNavGraph
import app.jardinageons.presentation.features.seedInventory.SeedInventoryScreen
import app.jardinageons.presentation.features.vegetable.VegetableScreen
import app.jardinageons.presentation.theme.JardinageonsTheme
import java.io.File
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import app.jardinageons.data.workers.SyncWorker
import app.jardinageons.data.workers.WateringWorker

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

        val wateringWorkRequest = PeriodicWorkRequestBuilder<WateringWorker>(3, TimeUnit.DAYS).build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "wateringReminders",
            ExistingPeriodicWorkPolicy.KEEP,
            wateringWorkRequest
        )

        // Restaurer le token depuis le DataStore au démarrage
        lifecycleScope.launch {
            this@MainActivity.tokenDataStore.data.collect { tokens ->
                TokenManager.accessToken = tokens?.accessToken
                TokenManager.refreshToken = tokens?.refreshToken
            }
        }

        setContent {
            JardinageonsTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginClick = { email, password ->
                                lifecycleScope.launch(Dispatchers.IO) {
                                    try {
                                        val response = RetrofitClient.loginQService.login(
                                            LoginRequest(email = email, password = password)
                                        )
                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                val loginData = response.body()
                                                if (loginData != null) {
                                                    TokenManager.accessToken = loginData.accessToken
                                                    TokenManager.refreshToken = loginData.refreshToken
                                                    this@MainActivity.tokenDataStore.updateData { currentTokens ->
                                                        Tokens(
                                                            tokenType = loginData.tokenType,
                                                            accessToken = loginData.accessToken,
                                                            expiresIn = loginData.expiresIn,
                                                            refreshToken = loginData.refreshToken
                                                        )
                                                    }
                                                }
                                                navController.navigate("home")
                                            } else {
                                                Toast.makeText(this@MainActivity, "Erreur de connexion : ${response.code()}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(this@MainActivity, "Erreur réseau : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            onRegisterClick = {
                                navController.navigate("register")
                            }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onLoginClick = {
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onRegisterClick = { email, password ->
                                lifecycleScope.launch(Dispatchers.IO) {
                                    try {
                                        val response = RetrofitClient.loginQService.register(
                                            LoginRequest(email = email, password = password)
                                        )
                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(this@MainActivity, "Compte créé ! Connectez-vous.", Toast.LENGTH_SHORT).show()
                                                navController.navigate("login") {
                                                    popUpTo("register") { inclusive = true }
                                                }
                                            } else {
                                                Toast.makeText(this@MainActivity, "Erreur d'inscription : ${response.code()}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(this@MainActivity, "Erreur réseau : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )
                    }
                    composable("home") {
                        HomeScreen()
                            AppNavGraph()
                    }
                }
            }
        }
    }
}
