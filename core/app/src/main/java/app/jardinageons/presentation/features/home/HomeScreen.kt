package app.jardinageons.presentation.features.home

import AnimatedPlantLoader
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.R
import app.jardinageons.presentation.components.TipCard
import app.jardinageons.presentation.features.weather.WeatherViewModel
import app.jardinageons.presentation.features.weather.components.WeatherWidget
import java.util.Calendar

@Composable
fun HomeScreen(
    weatherViewModel: WeatherViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val advices by homeViewModel.advices.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    // Récupération du mois en cours
    val currentMonthStr = remember {
        val months = listOf(
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        )
        months[Calendar.getInstance().get(Calendar.MONTH)]
    }

    val filteredAdvices = remember(advices) {
        val normalizedCurrentMonth = currentMonthStr.lowercase().replace("é", "e").replace("û", "u")

        advices.filter { advice ->
            val normalizedApiMonth = advice.month?.lowercase()?.replace("é", "e")?.replace("û", "u")
            normalizedApiMonth == normalizedCurrentMonth
        }.take(2)
    }

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
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

    // Déclenchement automatique du dialogue si la permission n'est pas accordée
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

            // 1. Section Météo
            item {
                SectionTitle("Météo en direct")
                WeatherWidget(
                    viewModel = weatherViewModel,
                    modifier = Modifier.padding(top = 0.dp)
                )
            }


            // 2. Section Mon Jardin
            // 2. Section Notifications (Nouveau !)
            item {
                SectionTitle("Restez informé")
                NotificationPromoCard(
                    hasPermission = hasNotificationPermission,
                    onClick = {
                        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            showDialog = true
                        }
                    }
                )
            }

            // 3. Section Image du Jardin
            item {
                SectionTitle("Mon Jardin")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.LightGray)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.potted_plant),
                        contentDescription = "Mon Jardin",
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.Center),
                        alpha = 0.5f
                    )
                }
            }

            // 3    . Section Astuces du Mois
            item {
                SectionTitle("Astuces de $currentMonthStr")
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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
                    // Définition des couleurs et de l'icône selon la position
                    val gradient = if (index % 2 == 0) {
                        listOf(Color(0xFF11998e), Color(0xFF38ef7d))
                    } else {
                        listOf(Color(0xFFec008c), Color(0xFFfc6767))
                    }
                    val icon = if (index % 2 == 0) "🌱" else "🌸"

                    // Appel du TipCard
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
        icon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF4CAF50)) },
        title = { Text(text = "Activer les rappels ?", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) },
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
            ) { Text("Confirmer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Plus tard", color = Color.Gray) }
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
                    text = if (hasPermission) "Vous recevrez des rappels tous les 3 jours." else "Cliquez pour ne plus rien oublier !",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
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
        color = Color(0xFF1C1C1E), // Noir très foncé pour plus de modernité
        modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 16.dp)
    )
}

@Composable
private fun TipCard(
    category: String,
    description: String,
    icon: String,
    gradientColors: List<Color>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(colors = gradientColors))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 40.dp, y = 40.dp)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
            )
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = category.uppercase(),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = description,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}