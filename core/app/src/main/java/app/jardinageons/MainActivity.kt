package app.jardinageons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.jardinageons.presentation.features.seedInventory.SeedInventoryScreen
import app.jardinageons.presentation.theme.JardinageonsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JardinageonsTheme {
                SeedInventoryScreen()
            }
        }
    }
}
