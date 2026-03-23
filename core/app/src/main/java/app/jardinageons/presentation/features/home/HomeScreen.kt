package app.jardinageons.presentation.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.R
import app.jardinageons.presentation.features.garden.GardenViewModel
import app.jardinageons.presentation.features.garden.components.GardenPlanView
import app.jardinageons.presentation.features.garden.model.GardenCanvasModel
import app.jardinageons.presentation.features.weather.WeatherViewModel
import app.jardinageons.presentation.features.weather.components.WeatherWidget

@Composable
fun HomeScreen(
    weatherViewModel: WeatherViewModel = viewModel(),
    gardenViewModel: GardenViewModel = viewModel(),
    onGardenClick: () -> Unit = {}
) {
    val gardenUiState by gardenViewModel.uiState.collectAsState()

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
                SectionTitle("Mon Jardin")
                HomeGardenCard(
                    canvasModel = gardenUiState.selectedCanvas,
                    isLoading = gardenUiState.isLoading,
                    onClick = onGardenClick
                )
            }


            item {
                SectionTitle("Astuces du jardinier")

                TipCard(
                    category = "Astuce de saison",
                    description = "Arrosez tôt le matin pour éviter l'évaporation et prévenir les maladies fongiques.",
                    icon = "🌱",
                    gradientColors = listOf(Color(0xFF11998e), Color(0xFF38ef7d))
                )

                Spacer(modifier = Modifier.height(16.dp))

                TipCard(
                    category = "Compagnonnage",
                    description = "Plantez des œillets d'Inde ou du basilic près de vos tomates pour éloigner naturellement les pucerons.",
                    icon = "🌸",
                    gradientColors = listOf(Color(0xFFec008c), Color(0xFFfc6767))
                )
            }
        }
    }
}

@Composable
private fun HomeGardenCard(
    canvasModel: GardenCanvasModel?,
    isLoading: Boolean,
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
