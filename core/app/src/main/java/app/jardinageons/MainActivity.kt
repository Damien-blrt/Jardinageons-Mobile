package app.jardinageons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.jardinageons.data.storage.TokenManager
import app.jardinageons.data.storage.tokenDataStore
import app.jardinageons.presentation.components.AppNavGraph
import app.jardinageons.presentation.components.AuthorizeView
import app.jardinageons.presentation.features.login.AuthState
import app.jardinageons.presentation.features.login.AuthViewModel
import app.jardinageons.presentation.features.login.LoginScreen
import app.jardinageons.presentation.features.register.RegisterScreen
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
                        val authViewModel: AuthViewModel = viewModel()
                        val authState by authViewModel.state.collectAsStateWithLifecycle()
                        val navController = rememberNavController()
                        val snackbarHostState = remember { SnackbarHostState() }

                        LaunchedEffect(authState) {
                            when (authState) {
                                is AuthState.RegisterSuccess -> {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                    authViewModel.resetState()
                                }
                                is AuthState.Error -> {
                                    snackbarHostState.showSnackbar((authState as AuthState.Error).message)
                                    authViewModel.resetState()
                                }
                                else -> {}
                            }
                        }

                        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
                            NavHost(navController = navController, startDestination = "login") {
                                composable("login") {
                                    LoginScreen(
                                        onLoginClick = { email, password ->
                                            authViewModel.login(email, password)
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
                                            authViewModel.register(email, password)
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}
