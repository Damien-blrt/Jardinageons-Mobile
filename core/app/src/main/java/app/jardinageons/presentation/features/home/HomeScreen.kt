package app.jardinageons.presentation.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.R
import app.jardinageons.presentation.features.weather.WeatherViewModel
import app.jardinageons.presentation.features.weather.components.WeatherWidget
import app.jardinageons.presentation.theme.DarkGreen
import app.jardinageons.presentation.theme.LightGreen

@Composable
fun HomeScreen(
    weatherViewModel: WeatherViewModel = viewModel()
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Section Météo
            item {
                WeatherWidget(
                    viewModel = weatherViewModel,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Section Image du Jardin (Placeholder pour l'instant)
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
                    // Ici on pourra mettre une vraie image plus tard
                    Image(
                        painter = painterResource(id = R.drawable.potted_plant), // Icone par défaut
                        contentDescription = "Mon Jardin",
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.Center),
                        alpha = 0.5f
                    )
                }
            }

            // Section Astuces du Jardinier (Placeholder pour l'instant) en attendant le CRUD Conseil
            item {
                SectionTitle("Astuces du jour")
                TipCard(
                    title = "Arrosage matinal",
                    description = "Arrosez tôt le matin pour éviter l'évaporation et les maladies fongiques.",
                    icon = "💧"
                )
                Spacer(modifier = Modifier.height(8.dp))
                TipCard(
                    title = "Paillage",
                    description = "Paillez vos pieds de tomates pour garder l'humidité au sol.",
                    icon = "🌾"
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
        modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp)
    )
}

@Composable
private fun TipCard(title: String, description: String, icon: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(listOf(LightGreen, DarkGreen))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 24.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
