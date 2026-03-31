package app.jardinageons.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.jardinageons.presentation.features.home.HomeScreen
import app.jardinageons.presentation.features.seedInventory.SeedInventoryScreen
import app.jardinageons.presentation.features.statPage.StatScreen

@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    var selectedRoute by rememberSaveable {
        mutableStateOf(BottomBarRoutes.HOME)
    }

    Scaffold(
        bottomBar = {
            AppBottomBar(
                selectedRoute = selectedRoute,
                onDestinationSelected = { route ->
                    selectedRoute = route
                }
            )
        },
    ) { padding ->
        Box(
            modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedRoute) {
                BottomBarRoutes.HOME -> {
                    HomeScreen()
                }
                BottomBarRoutes.GARDEN -> {
                    // TODO(GardenScreen): remplacer cet écran temporaire par GardenScreen()
                    PlaceholderScreen(title = "Potager")
                }
                BottomBarRoutes.INVENTORY -> SeedInventoryScreen()
                BottomBarRoutes.HISTORY -> {
                    // TODO(HistoryScreen): remplacer cet écran temporaire par HistoryScreen()
                    PlaceholderScreen(title = "Historique")
                }

                BottomBarRoutes.STATS -> StatScreen()
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$title (à faire)",
            style = MaterialTheme.typography.titleLarge
        )
    }
}
