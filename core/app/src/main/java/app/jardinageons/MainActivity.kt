package app.jardinageons

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import app.jardinageons.data.models.LoginRequest
import app.jardinageons.data.services.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.jardinageons.presentation.features.login.LoginScreen
import app.jardinageons.presentation.features.register.RegisterScreen
import app.jardinageons.presentation.features.seedInventory.SeedInventoryScreen
import app.jardinageons.presentation.theme.JardinageonsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                                                navController.navigate("seed_inventory")
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
                    composable("seed_inventory") {
                        SeedInventoryScreen()
                    }
                }
            }
        }
    }
}
