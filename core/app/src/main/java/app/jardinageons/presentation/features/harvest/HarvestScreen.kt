package app.jardinageons.presentation.features.harvest

import AnimatedPlantLoader
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.data.models.Harvest
import app.jardinageons.presentation.features.harvest.HarvestEvent.*

@Composable
fun HarvestScreen(viewModel: HarvestViewModel = viewModel()){
    val harvestList by viewModel.harvests.collectAsState()
    val totalHarvests by viewModel.totalHarvests.collectAsState()
    val isLoading by viewModel.isFirstLoading.collectAsState()
    var selectedHarvestForEdit by remember { mutableStateOf<Harvest?>(null)}
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            when (message) {
                modifiedSuccess -> snackbarHostState.showSnackbar("Récolte modifiée avec succès")
                deleteSuccess -> snackbarHostState.showSnackbar("Récolte supprimée avec succès")
                modifiedError -> snackbarHostState.showSnackbar("Erreur : Récolte non modifiée")
                deleteError -> snackbarHostState.showSnackbar("Erreur : Récolte non supprimée")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isLoading) {
            AnimatedPlantLoader()
        } else {
            Box(modifier = Modifier.fillMaxSize().padding()) {

            }
        }

    }
}