package app.jardinageons.presentation.features.seedInventory

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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SeedInventoryScreen(
    isLoading: Boolean,
    viewModel: SeedInventoryViewModel = viewModel()
) {
    val seedList by viewModel.seeds.collectAsState()
    val totalSeeds by viewModel.totalSeeds.collectAsState()
    val averageGerminationTime by viewModel.averageGerminationTime.collectAsState()

    var selectedSeedForEdit by remember { mutableStateOf<Seed?>(null) }
    var createButtonClicked by remember { mutableStateOf<Boolean>(false) }


    var searchedSeedName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
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
                            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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
                            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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
                                listOf(
                                    Color(0xFF66BB6A),
                                    Color(0xFF43A047)
                                )
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            value = "${totalSeeds}",
                            label = "Graines total",
                            gradient = Brush.verticalGradient(
                                listOf(
                                    Color(0xFFFFA726),
                                    Color(0xFFFB8C00)
                                )
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            value = "${averageGerminationTime}j",
                            label = "Germination moy.",
                            gradient = Brush.verticalGradient(
                                listOf(
                                    Color(0xFF7E8FF7),
                                    Color(0xFFAB47BC)
                                )
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