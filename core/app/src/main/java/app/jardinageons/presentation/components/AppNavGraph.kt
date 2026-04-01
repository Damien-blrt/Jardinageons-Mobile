package app.jardinageons.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.presentation.features.garden.GardenScreen
import app.jardinageons.presentation.features.garden.GardenViewModel
import app.jardinageons.presentation.features.harvest.HarvestScreen
import app.jardinageons.presentation.features.home.HomeScreen
import app.jardinageons.presentation.features.seedInventory.SeedInventoryScreen
import app.jardinageons.presentation.features.statPage.StatScreen
import app.jardinageons.presentation.features.vegetable.VegetableScreen

@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val gardenViewModel: GardenViewModel = viewModel()
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
                    HomeScreen(
                        gardenViewModel = gardenViewModel,
                        onGardenClick = { selectedRoute = BottomBarRoutes.GARDEN }
                    )
                }

                BottomBarRoutes.GARDEN -> {
                    GardenScreen(viewModel = gardenViewModel)
                }

                BottomBarRoutes.INVENTORY -> SeedInventoryScreen()
                BottomBarRoutes.HISTORY -> HarvestScreen()
                BottomBarRoutes.VEGETABLE -> VegetableScreen()
                BottomBarRoutes.STATS -> StatScreen()
            }
        }
    }
}
