package app.jardinageons.presentation.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import app.jardinageons.presentation.features.weather.WeatherViewModel
import app.jardinageons.presentation.features.weather.components.WeatherWidget

@Composable
fun HomeScreen(
    weatherViewModel: WeatherViewModel = viewModel()
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp) // Un peu plus de marge en bas
        ) {
            // 1. Section Image du Jardin
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

            // 2. Section Météo
            item {
                SectionTitle("Météo en direct")
                WeatherWidget(
                    viewModel = weatherViewModel,
                    modifier = Modifier.padding(top = 0.dp)
                )
            }

            // 3. Section Astuces du Jardinier (Nouveau Design !)
            item {
                SectionTitle("Astuces du jardinier")

                // Carte Verte
                TipCard(
                    category = "Astuce de saison",
                    description = "Arrosez tôt le matin pour éviter l'évaporation et prévenir les maladies fongiques.",
                    icon = "🌱",
                    // Dégradé Vert vif vers Vert clair
                    gradientColors = listOf(Color(0xFF11998e), Color(0xFF38ef7d))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Carte Rose/Corail
                TipCard(
                    category = "Compagnonnage",
                    description = "Plantez des œillets d'Inde ou du basilic près de vos tomates pour éloigner naturellement les pucerons.",
                    icon = "🌸",
                    // Dégradé Fuchsia vers Corail
                    gradientColors = listOf(Color(0xFFec008c), Color(0xFFfc6767))
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

/**
 * Le nouveau composant TipCard stylisé "Glassmorphism"
 */
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
        shape = RoundedCornerShape(24.dp), // Bords très arrondis (24px)
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) // Ombre douce
    ) {
        // Box principale pour gérer le dégradé et le cercle décoratif en fond
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(colors = gradientColors))
        ) {
            // --- Élément décoratif (Cercle en bas à droite) ---
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 40.dp, y = 40.dp) // Décale le cercle vers l'extérieur
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)) // Blanc translucide
            )

            // --- Contenu principal ---
            Row(
                modifier = Modifier
                    .padding(20.dp) // Padding interne de 20px
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Colonne de Gauche : Icône avec effet de verre
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.2f)), // Effet Glassmorphism
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 2. Colonne de Droite : Textes
                Column(modifier = Modifier.weight(1f)) {
                    // En-tête (Ampoule + Catégorie)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = category.uppercase(),
                            color = Color.White.copy(alpha = 0.8f), // Blanc légèrement transparent
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp // Aère les lettres pour un effet moderne
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Corps de texte
                    Text(
                        text = description,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp // Interligne aéré
                    )
                }
            }
        }
    }
}