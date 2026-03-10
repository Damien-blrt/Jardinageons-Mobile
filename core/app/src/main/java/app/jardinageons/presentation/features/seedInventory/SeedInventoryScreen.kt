package app.jardinageons.presentation.features.seedInventory

import AnimatedPlantLoader
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
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
import app.jardinageons.data.models.Seed
import app.jardinageons.presentation.features.seedInventory.components.CreateSeedModal
import app.jardinageons.presentation.features.seedInventory.components.EditSeedModal
import app.jardinageons.presentation.features.seedInventory.components.SeedCard
import app.jardinageons.presentation.features.seedInventory.components.StatCard
import app.jardinageons.presentation.theme.Blue
import app.jardinageons.presentation.theme.DarkGreen
import app.jardinageons.presentation.theme.DarkOrange
import app.jardinageons.presentation.theme.LightBlue
import app.jardinageons.presentation.theme.LightGreen
import app.jardinageons.presentation.theme.LightOrange
import app.jardinageons.presentation.theme.Purple
import app.jardinageons.presentation.features.weather.WeatherViewModel
import app.jardinageons.presentation.features.weather.components.WeatherWidget
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SeedInventoryScreen(
    viewModel: SeedInventoryViewModel = viewModel()
) {
    val seedList by viewModel.seeds.collectAsState()
    val totalSeeds by viewModel.totalSeeds.collectAsState()
    val averageGerminationTime by viewModel.averageGerminationTime.collectAsState()

    var selectedSeedForEdit by remember { mutableStateOf<Seed?>(null) }
    var createButtonClicked by remember { mutableStateOf<Boolean>(false) }
    val isLoading by viewModel.isLoading.collectAsState()

    var searchedSeedName by remember { mutableStateOf("") }
    //val weatherViewModel: WeatherViewModel = viewModel()

    //doc : https://developer.android.com/develop/ui/compose/components/snackbar?hl=fr
    val snackbarHostState = remember { SnackbarHostState() }

    //doc : https://developer.android.com/develop/ui/compose/side-effects
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            when (message) {
                Event.addSuccess -> snackbarHostState.showSnackbar("Graine ajoutée avec succès")
                Event.modifiedSuccess -> snackbarHostState.showSnackbar("Graine modifiée avec succès")
                Event.deleteSuccess -> snackbarHostState.showSnackbar("Graine supprimée avec succès")
                Event.modifiedError -> snackbarHostState.showSnackbar("Erreur : graine non modifiée")
                Event.addError -> snackbarHostState.showSnackbar("Erreur : graine non ajoutée")
                Event.deleteError -> snackbarHostState.showSnackbar("Erreur : graine non supprimée")
            }


        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isLoading) {
            AnimatedPlantLoader()
        } else {
            Box(modifier = Modifier.fillMaxSize()) {

                /**
                 * La syntaxe selectedSeedForEdit?.let a été générée par une IA. De ce que j'ai compris
                 * c'est une manière plus sécurisée de vérifier si une variable est pas nulle avant de l'utiliser
                 * */
                selectedSeedForEdit?.let {
                    EditSeedModal(
                        seed = it,
                        onDismiss = { selectedSeedForEdit = null },
                        onSave = { updatedSeed ->
                            /**
                             * doc: https://developer.android.com/reference/java/text/DateFormat
                             */
                            val isoDate = try {
                                val inputFormat =
                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                val outputFormat =
                                    SimpleDateFormat(
                                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                        Locale.getDefault()
                                    )
                                val date = inputFormat.parse(updatedSeed.expiryDate)
                                outputFormat.format(date)
                            } catch (e: Exception) {
                                SimpleDateFormat(
                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                    Locale.getDefault()
                                ).format(
                                    Date()
                                )
                            }
                            val seed = updatedSeed.copy(expiryDate = isoDate)

                            viewModel.updateSeed(seed.id, seed)
                            selectedSeedForEdit = null
                        },
                        onDelete = { id ->
                            viewModel.deleteSeed(id)
                            selectedSeedForEdit = null
                        }
                    )
                }

                if (createButtonClicked) {
                    CreateSeedModal(
                        onDismiss = { createButtonClicked = false },
                        onSave = { newSeed ->
                            /**
                             * doc: https://developer.android.com/reference/java/text/DateFormat
                             */
                            val isoDate = try {
                                val inputFormat =
                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                val outputFormat =
                                    SimpleDateFormat(
                                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                        Locale.getDefault()
                                    )
                                val date = inputFormat.parse(newSeed.expiryDate)
                                outputFormat.format(date)
                            } catch (e: Exception) {
                                SimpleDateFormat(
                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                    Locale.getDefault()
                                ).format(
                                    Date()
                                )
                            }

                            val request = SeedRequest(
                                name = newSeed.name,
                                quantity = newSeed.quantity,
                                germinationTime = newSeed.germinationTime,
                                description = newSeed.description,
                                vegetableId = 1,
                                expiryDate = isoDate
                            )

                            viewModel.createSeed(request)
                            createButtonClicked = false
                        }
                    )
                }
                LazyColumn(Modifier.padding(10.dp)) {

                    //item {
                    //WeatherWidget(
                    //viewModel = weatherViewModel,
                    //modifier = Modifier.padding(bottom = 16.dp)
                    //)
                    //}
                    item {
                        OutlinedTextField(
                            value = searchedSeedName,
                            onValueChange = { newText ->
                                searchedSeedName = newText
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            },
                            label = { Text("Rechercher une variété") },
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
                                value = "${seedList.size}",
                                label = "Variétés",
                                gradient = Brush.verticalGradient(
                                    listOf(LightGreen, DarkGreen)
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            StatCard(
                                value = "${totalSeeds}",
                                label = "Graines total",
                                gradient = Brush.verticalGradient(
                                    listOf(LightOrange, DarkOrange)
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            StatCard(
                                value = "${averageGerminationTime}j",
                                label = "Germination moy.",
                                gradient = Brush.verticalGradient(
                                    listOf(LightBlue, Purple)
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
                            text = "Mon inventaire", fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(items = seedList) { seed ->
                        SeedCard(seed = seed, color = Color(0xFFFB2B37), onClick = {
                            selectedSeedForEdit = seed
                        })
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