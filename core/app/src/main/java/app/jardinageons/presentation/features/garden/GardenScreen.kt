package app.jardinageons.presentation.features.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.R
import app.jardinageons.data.models.Garden
import app.jardinageons.presentation.features.garden.components.GardenPlanView


/*
Feature réalisée avec l'aide de l'IA par manque de temps
 */
@Composable
fun GardenScreen(
    viewModel: GardenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCanvas = uiState.selectedCanvas
    val selectedGarden = uiState.gardens
        .firstOrNull { it.id == uiState.selectedGardenId }
        ?: uiState.gardens.firstOrNull()

    if (uiState.isLoading) {
        GardenLoadingState()
        return
    }

    val errorMessage = uiState.errorMessage
    if (errorMessage != null && uiState.gardens.isEmpty()) {
        GardenErrorState(
            message = errorMessage,
            onRetry = viewModel::loadGardens
        )
        return
    }

    if (uiState.gardens.isEmpty()) {
        GardenUnavailableState(onRetry = viewModel::loadGardens)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.garden_title),
            style = MaterialTheme.typography.titleLarge
        )

        GardenSelector(
            selectedGardenName = selectedGarden?.name.orEmpty(),
            gardens = uiState.gardens,
            onGardenSelected = viewModel::selectGarden
        )

        if (selectedCanvas == null) {
            GardenPlanPlaceholder(
                message = stringResource(R.string.garden_no_plan),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } else {
            GardenPlanView(
                canvasModel = selectedCanvas,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }

        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun GardenLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun GardenUnavailableState(
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.garden_unavailable))
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.common_retry))
            }
        }
    }
}

@Composable
private fun GardenErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.common_retry))
            }
        }
    }
}

@Composable
private fun GardenSelector(
    selectedGardenName: String,
    gardens: List<Garden>,
    onGardenSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedGardenName,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.garden_selector_label)) },
            trailingIcon = { Text(if (isExpanded) "▲" else "▼") },
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { isExpanded = true }
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            gardens.forEach { garden ->
                DropdownMenuItem(
                    text = { Text(garden.name) },
                    onClick = {
                        onGardenSelected(garden.id)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun GardenPlanPlaceholder(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFF4E6)),
        contentAlignment = Alignment.Center
    ) {
        Text(message)
    }
}
