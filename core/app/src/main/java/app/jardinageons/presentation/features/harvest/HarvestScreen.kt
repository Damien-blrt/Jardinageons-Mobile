package app.jardinageons.presentation.features.harvest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.data.models.Harvest
import app.jardinageons.presentation.components.AnimatedPlantLoader
import app.jardinageons.presentation.features.harvest.HarvestEvent.*
import app.jardinageons.presentation.features.harvest.components.EditHarvestModal
import app.jardinageons.presentation.features.harvest.components.HarvestCard
import app.jardinageons.presentation.features.seedInventory.components.StatCard
import app.jardinageons.presentation.theme.DarkGreen
import app.jardinageons.presentation.theme.DarkOrange
import app.jardinageons.presentation.theme.LightGreen
import app.jardinageons.presentation.theme.LightOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HarvestScreen(viewModel: HarvestViewModel = viewModel()) {
    val harvestList by viewModel.harvests.collectAsStateWithLifecycle()
    val totalHarvests by viewModel.totalHarvests.collectAsStateWithLifecycle()
    val isLoading by viewModel.isFirstLoading.collectAsStateWithLifecycle()
    var selectedHarvestForEdit by remember { mutableStateOf<Harvest?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    var searchQuery by remember { mutableStateOf("") }

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

    val filteredHarvests = remember(harvestList, searchQuery) {
        if (searchQuery.isBlank()) harvestList
        else harvestList.filter {
            it.description.contains(searchQuery, ignoreCase = true) ||
                    it.date.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isLoading) {
            AnimatedPlantLoader()
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {

                selectedHarvestForEdit?.let { harvest ->
                    EditHarvestModal(
                        harvest = harvest,
                        onDismiss = { selectedHarvestForEdit = null },
                        onSave = { updatedHarvest ->
                            val isoDate = try {
                                val inputFormat =
                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                val outputFormat =
                                    SimpleDateFormat(
                                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                        Locale.getDefault()
                                    )
                                val date = inputFormat.parse(updatedHarvest.date)
                                outputFormat.format(date)
                            } catch (e: Exception) {
                                SimpleDateFormat(
                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                    Locale.getDefault()
                                ).format(Date())
                            }
                            val harvestToSave = updatedHarvest.copy(date = isoDate)
                            viewModel.updateHarvest(harvestToSave.id, harvestToSave)
                            selectedHarvestForEdit = null
                        },
                        onDelete = { id ->
                            viewModel.deleteHarvest(id)
                            selectedHarvestForEdit = null
                        }
                    )
                }

                LazyColumn(Modifier.padding(10.dp)) {
                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { newText -> searchQuery = newText },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            },
                            label = { Text("Rechercher une récolte") },
                            shape = RoundedCornerShape(15.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Green,
                                focusedLeadingIconColor = Color.Green
                            ),
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .fillMaxWidth()
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                                StatCard(
                                    value = "${filteredHarvests.size}",
                                    label = "Récoltes",
                                    gradient = Brush.verticalGradient(
                                        listOf(LightGreen, DarkGreen)
                                    ),
                                    modifier = Modifier.weight(1f)

                                )

                                StatCard(
                                    value = "${totalHarvests}",
                                    label = "Kg total",
                                    gradient = Brush.verticalGradient(
                                        listOf(LightOrange, DarkOrange)
                                    ) ,
                                    modifier = Modifier.weight(1f)
                                )

                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item {
                        Text(
                            text = "Mes récoltes", fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(items = filteredHarvests) { harvest ->
                        HarvestCard(
                            harvest = harvest,
                            color = Color(0xFF46A24A),
                            onClick = { selectedHarvestForEdit = harvest }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}