package app.jardinageons.presentation.features.home

import AnimatedPlantLoader
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1C1C1E),
        modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 16.dp)
    )
}