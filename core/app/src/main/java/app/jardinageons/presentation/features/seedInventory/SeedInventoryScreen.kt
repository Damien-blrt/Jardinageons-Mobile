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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.R
import app.jardinageons.data.models.Seed
import app.jardinageons.presentation.components.AnimatedPlantLoader
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

@Composable
fun SeedInventoryScreen(
    viewModel: SeedInventoryViewModel = viewModel()
) {
    val seedList by viewModel.seeds.collectAsStateWithLifecycle()
    val totalSeeds by viewModel.totalSeeds.collectAsStateWithLifecycle()
    val averageGerminationTime by viewModel.averageGerminationTime.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var selectedSeedForEdit by remember { mutableStateOf<Seed?>(null) }
    var createButtonClicked by remember { mutableStateOf(false) }
    var searchedSeedName by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    val msgAddSuccess = stringResource(R.string.seed_added_success)
    val msgModifiedSuccess = stringResource(R.string.seed_modified_success)
    val msgDeleteSuccess = stringResource(R.string.seed_deleted_success)
    val msgModifiedError = stringResource(R.string.seed_modified_error)
    val msgAddError = stringResource(R.string.seed_added_error)
    val msgDeleteError = stringResource(R.string.seed_deleted_error)

    //doc : https://developer.android.com/develop/ui/compose/side-effects
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            when (message) {
                Event.addSuccess -> snackbarHostState.showSnackbar(msgAddSuccess)
                Event.modifiedSuccess -> snackbarHostState.showSnackbar(msgModifiedSuccess)
                Event.deleteSuccess -> snackbarHostState.showSnackbar(msgDeleteSuccess)
                Event.modifiedError -> snackbarHostState.showSnackbar(msgModifiedError)
                Event.addError -> snackbarHostState.showSnackbar(msgAddError)
                Event.deleteError -> snackbarHostState.showSnackbar(msgDeleteError)
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

                selectedSeedForEdit?.let {
                    EditSeedModal(
                        seed = it,
                        onDismiss = { selectedSeedForEdit = null },
                        onSave = { updatedSeed ->
                            viewModel.updateSeed(updatedSeed.id, updatedSeed)
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
                            viewModel.createSeed(newSeed)
                            createButtonClicked = false
                        }
                    )
                }

                val filteredSeeds = remember(seedList, searchedSeedName) {
                    if (searchedSeedName.isBlank()) seedList
                    else seedList.filter { it.name.contains(searchedSeedName, ignoreCase = true) }
                }

                LazyColumn(Modifier.padding(10.dp)) {
                    item {
                        OutlinedTextField(
                            value = searchedSeedName,
                            onValueChange = { searchedSeedName = it },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            },
                            label = { Text(stringResource(R.string.seed_search)) },
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
                                label = stringResource(R.string.seed_varieties),
                                gradient = Brush.verticalGradient(listOf(LightGreen, DarkGreen)),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                value = "$totalSeeds",
                                label = stringResource(R.string.seed_total),
                                gradient = Brush.verticalGradient(listOf(LightOrange, DarkOrange)),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                value = "${averageGerminationTime}j",
                                label = stringResource(R.string.seed_avg_germination),
                                gradient = Brush.verticalGradient(listOf(LightBlue, Purple)),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(12.dp)) }
                    item {
                        Text(
                            text = stringResource(R.string.seed_my_inventory),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    items(items = filteredSeeds, key = { it.id }) { seed ->
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
