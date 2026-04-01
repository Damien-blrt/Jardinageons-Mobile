package app.jardinageons.presentation.features.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.R
import app.jardinageons.presentation.features.garden.model.GardenCanvasModel
import app.jardinageons.presentation.components.AnimatedPlantLoader
import app.jardinageons.presentation.components.TipCard
import app.jardinageons.presentation.features.garden.GardenViewModel
import app.jardinageons.presentation.features.garden.components.GardenPlanView
import app.jardinageons.presentation.features.weather.WeatherViewModel
import app.jardinageons.presentation.features.weather.components.WeatherWidget
import java.util.Calendar

@Composable
fun HomeScreen(
    weatherViewModel: WeatherViewModel = viewModel(),
    gardenViewModel: GardenViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    onGardenClick: () -> Unit = {}
) {
    val gardenUiState by gardenViewModel.uiState.collectAsStateWithLifecycle()
    val advices by homeViewModel.advices.collectAsStateWithLifecycle()
    val isLoading by homeViewModel.isLoading.collectAsStateWithLifecycle()

    val currentMonthStr = remember {
        val months = listOf(
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        )
        months[Calendar.getInstance().get(Calendar.MONTH)]
    }

    val filteredAdvices = remember(advices, currentMonthStr) {
        val normalizedCurrentMonth = currentMonthStr.lowercase()
            .replace("é", "e")
            .replace("û", "u")

        advices.filter { advice ->
            val normalizedApiMonth = advice.month
                ?.lowercase()
                ?.replace("é", "e")
                ?.replace("û", "u")
            normalizedApiMonth == normalizedCurrentMonth
        }.take(2)
    }

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            showDialog = true
        }
    }

    if (showDialog) {
        NotificationConfirmDialog(
            onConfirm = {
                showDialog = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            onDismiss = { showDialog = false }
        )
    }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                SectionTitle("Météo en direct")
                WeatherWidget(
                    viewModel = weatherViewModel,
                    modifier = Modifier.padding(top = 0.dp)
                )
            }

            item {
                SectionTitle("Restez informé")
                NotificationPromoCard(
                    hasPermission = hasNotificationPermission,
                    onClick = {
                        if (!hasNotificationPermission &&
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        ) {
                            showDialog = true
                        }
                    }
                )
            }

            item {
                SectionTitle("Mon Jardin")
                HomeGardenCard(
                    canvasModel = gardenUiState.selectedCanvas,
                    isLoading = gardenUiState.isLoading,
                    errorMessage = gardenUiState.errorMessage,
                    onClick = onGardenClick
                )
            }

            item {
                SectionTitle("Astuces de $currentMonthStr")
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedPlantLoader(modifier = Modifier.padding(16.dp))
                    }
                }
            } else if (filteredAdvices.isEmpty()) {
                item {
                    Text(
                        text = "Aucun conseil spécifique pour le mois de ${currentMonthStr.lowercase()}.",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = Color.Gray
                    )
                }
            } else {
                itemsIndexed(filteredAdvices) { index, adviceItem ->
                    val gradient = if (index % 2 == 0) {
                        listOf(Color(0xFF11998E), Color(0xFF38EF7D))
                    } else {
                        listOf(Color(0xFFEC008C), Color(0xFFFC6767))
                    }
                    val icon = if (index % 2 == 0) "🌱" else "🌸"

                    TipCard(
                        category = adviceItem.titre ?: "Général",
                        description = adviceItem.advice ?: "Pas de description.",
                        icon = icon,
                        gradientColors = gradient
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun NotificationConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        icon = {
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint = Color(0xFF4CAF50)
            )
        },
        title = {
            Text(
                text = "Activer les rappels ?",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                "Recevez des notifications pour ne plus oublier d'arroser vos légumes. Vous pouvez changer cela dans les réglages à tout moment.",
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Plus tard", color = Color.Gray)
            }
        }
    )
}

@Composable
fun NotificationPromoCard(hasPermission: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(enabled = !hasPermission) { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasPermission) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (hasPermission) Color(0xFF4CAF50) else Color(0xFFFF9800)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = if (hasPermission) "Notifications Activées" else "Rappels d'arrosage",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = if (hasPermission) {
                        "Vous recevrez des rappels tous les 3 jours."
                    } else {
                        "Cliquez pour ne plus rien oublier !"
                    },
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
private fun HomeGardenCard(
    canvasModel: GardenCanvasModel?,
    isLoading: Boolean,
    errorMessage: String?,
    onClick: () -> Unit
) {
    val cardModifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(horizontal = 16.dp)

    if (canvasModel != null) {
        GardenPlanView(
            canvasModel = canvasModel,
            modifier = cardModifier.clickable(onClick = onClick),
            cornerRadius = 24.dp,
            contentPadding = 6.dp
        )
        return
    }

    Box(
        modifier = cardModifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.LightGray)
            .clickable(onClick = onClick)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.potted_plant),
                    contentDescription = "Mon Jardin",
                    modifier = Modifier.size(64.dp),
                    alpha = 0.5f
                )
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1C1C1E),
        modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 16.dp)
    )
}
