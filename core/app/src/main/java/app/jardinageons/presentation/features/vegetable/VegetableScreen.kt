package app.jardinageons.presentation.features.vegetable

import app.jardinageons.presentation.components.AnimatedPlantLoader
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.data.models.Vegetable
import app.jardinageons.presentation.features.seedInventory.components.StatCard
import app.jardinageons.presentation.features.vegetable.components.CreateVegetableModal
import app.jardinageons.presentation.features.vegetable.components.EditVegetableModal
import app.jardinageons.presentation.features.vegetable.components.VegetableCard
import app.jardinageons.presentation.theme.Blue
import app.jardinageons.presentation.theme.DarkGreen
import app.jardinageons.presentation.theme.LightGreen

@Composable
fun VegetableScreen(
    viewModel: VegetableViewModel = viewModel()
) {
    val vegetableList by viewModel.vegetables.collectAsStateWithLifecycle()
    val totalVegetables by viewModel.totalVegetables.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var selectedVegetableForEdit by remember { mutableStateOf<Vegetable?>(null) }
    var createButtonClicked by remember { mutableStateOf(false) }
    var searchedVegetableName by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                Event.addSuccess -> snackbarHostState.showSnackbar("Légume ajouté avec succès")
                Event.modifiedSuccess -> snackbarHostState.showSnackbar("Légume modifié avec succès")
                Event.deleteSuccess -> snackbarHostState.showSnackbar("Légume supprimé avec succès")
                Event.modifiedError -> snackbarHostState.showSnackbar("Erreur : légume non modifié")
                Event.addError -> snackbarHostState.showSnackbar("Erreur : légume non ajouté")
                Event.deleteError -> snackbarHostState.showSnackbar("Erreur : légume non supprimé")
            }
        }
    }

    val filteredVegetables = remember(vegetableList, searchedVegetableName) {
        vegetableList.filter { it.name.contains(searchedVegetableName, ignoreCase = true) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isLoading) {
            AnimatedPlantLoader()
        } else {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {

                selectedVegetableForEdit?.let { vegetable ->
                    EditVegetableModal(
                        vegetable = vegetable,
                        onDismiss = { selectedVegetableForEdit = null },
                        onSave = { updatedVegetable ->
                            viewModel.updateVegetable(updatedVegetable.id, updatedVegetable)
                            selectedVegetableForEdit = null
                        },
                        onDelete = { id ->
                            viewModel.deleteVegetable(id)
                            selectedVegetableForEdit = null
                        }
                    )
                }

                if (createButtonClicked) {
                    CreateVegetableModal(
                        onDismiss = { createButtonClicked = false },
                        onSave = { newVegetableRequest ->
                            viewModel.createVegetable(newVegetableRequest)
                            createButtonClicked = false
                        }
                    )
                }

                LazyColumn(Modifier.padding(10.dp)) {
                    item {
                        OutlinedTextField(
                            value = searchedVegetableName,
                            onValueChange = { searchedVegetableName = it },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            },
                            label = { Text("Rechercher un légume") },
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
                                value = "${vegetableList.size}",
                                label = "Variétés",
                                gradient = Brush.verticalGradient(
                                    listOf(LightGreen, DarkGreen)
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            StatCard(
                                value = "$totalVegetables",
                                label = "Total légumes",
                                gradient = Brush.verticalGradient(
                                    listOf(LightGreen, DarkGreen)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        Text(
                            text = "Catalogue des légumes",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(items = filteredVegetables) { vegetable ->
                        VegetableCard(
                            vegetable = vegetable,
                            color = Color(0xFF4CAF50),
                            onClick = {
                                selectedVegetableForEdit = vegetable
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Button(
                    onClick = { createButtonClicked = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp)
                        .size(64.dp),
                    shape = RoundedCornerShape(100.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}