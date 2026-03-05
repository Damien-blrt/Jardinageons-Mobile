package app.jardinageons.presentation.features.vegetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.jardinageons.data.services.RetrofitClient.vegetableService

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.data.models.Vegetable
import app.jardinageons.presentation.features.vegetable.components.VegetableViewModel

@Composable
fun VegetableInventory(
    viewModel: VegetableViewModel = viewModel()
) {
    val vegetables by viewModel.vegetables.collectAsState()
    val rainForecast by viewModel.rainForecast.collectAsState()

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = vegetables) { veg ->
            VegetableCard(vegetable = veg, rainForecast = rainForecast)
        }
    }
}

@Composable
fun VegetableCard(vegetable: Vegetable, rainForecast: Double?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = vegetable.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (rainForecast != null) {
                val needs = vegetable.waterNeedsMm ?: 5.0
                val deficit = needs - rainForecast

                if (deficit > 0) {
                    Text(
                        text = "⚠️ Il manque ${String.format("%.1f", deficit)} mm d'eau. Arrosage conseillé.",
                        color = Color(0xFFD32F2F), // Rouge
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "✅ Pas besoin d'arroser, la pluie est suffisante.",
                        color = Color(0xFF388E3C), // Vert
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Text("Chargement des données météo...", color = Color.Gray)
            }
        }
    }
}