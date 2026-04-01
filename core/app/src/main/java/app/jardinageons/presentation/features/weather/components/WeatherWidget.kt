package app.jardinageons.presentation.features.weather.components

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.jardinageons.data.models.WeatherSummary
import app.jardinageons.presentation.features.weather.WeatherViewModel

@SuppressLint("MissingPermission")
@Composable
fun WeatherWidget(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.weatherSummary.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Outil de Google pour lire le GPS
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Pour ne demander qu'une seule fois au lancement
    var hasRequestedLocation by remember { mutableStateOf(false) }

    // Le "Lanceur" de la boîte de dialogue de permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.fetchWeather(location.latitude, location.longitude)
                } else {
                    viewModel.fetchWeather(45.78, 3.08) // Clermont-Ferrand : si GPS désactivé
                }
            }
        } else {
            viewModel.fetchWeather(45.78, 3.08) // Clermont-Ferrand
        }
    }

    LaunchedEffect(Unit) {
        if (!hasRequestedLocation) {
            hasRequestedLocation = true
            // On affiche la boîte de dialogue Android
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val colorLightBlue = Color(0xFF4facfe)
    val colorDarkBlue = Color(0xFF4932d1)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(colorLightBlue, colorDarkBlue)
                    )
                )
        ) {
            when (summary) {
                null -> LoadingState()
                else -> WeatherContent(summary!!)
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Météo en cours...", color = Color.White)
    }
}

@Composable
private fun WeatherContent(summary: WeatherSummary) {
    val isRainy = summary.rainTotal24h > 0.0

    val colorYellow = Color(0xFFf1bc00)
    val colorDarkText = Color(0xFF1C1C1E)

    val mainCondition = if (isRainy) "Pluvieux" else "Nuageux"
    val mainIcon = if (isRainy) "🌧️" else "⛅"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = app.jardinageons.R.drawable.map_pin),
                        contentDescription = "Icône vent",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = summary.locationName,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                val tempDisplay = summary.currentTemp?.let { String.format("%.0f", it) } ?: "--"
                Text(
                    text = "$tempDisplay°C",
                    color = Color.White,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = mainIcon,
                    fontSize = 64.sp
                )
                Text(
                    text = mainCondition,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.2f))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = app.jardinageons.R.drawable.watter),
                        contentDescription = "Icône de pluie",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pluie prévue (24h) : ${String.format("%.1f", summary.rainTotal24h)} mm",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val windText = summary.windSpeedKmh?.let { "${String.format("%.0f", it)} km/h" } ?: "-- km/h"
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = app.jardinageons.R.drawable.wind),
                            contentDescription = "Icône vent",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                    )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = windText, color = Color.White)
                    }


                    val humidityText = summary.humidity?.let { "$it%" } ?: "--%"
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = app.jardinageons.R.drawable.droplets),
                            contentDescription = "Icône humidité",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = humidityText, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val adviceText = if (isRainy) {
            "Inutile d'arroser, la pluie s'en charge !"
        } else {
            "Pas de pluie prévue, pensez à arroser."
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colorYellow)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💡", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = adviceText,
                    color = colorDarkText,
                    fontSize = 15.sp
                )
            }
        }
    }
}