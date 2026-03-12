package app.jardinageons.presentation.features.statPage

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.data.models.Seed
import app.jardinageons.data.models.Vegetable
import app.jardinageons.presentation.features.seedInventory.SeedInventoryViewModel
import app.jardinageons.presentation.theme.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// ─── Couleurs thème jardinage ───────────────────────────────────────────────
private val GreenLight   = Color(0xFF81C784)
private val GreenDark    = Color(0xFF2E7D32)
private val OrangeChart  = Color(0xFFFF8F00)
private val BlueChart    = Color(0xFF1976D2)
private val TealChart    = Color(0xFF00897B)
private val PinkChart    = Color(0xFFE91E63)
private val PurpleChart  = Color(0xFF7B1FA2)
private val AmberChart   = Color(0xFFFFC107)

private val chartColors = listOf(
    GreenLight, OrangeChart, BlueChart, TealChart,
    PinkChart, PurpleChart, AmberChart, GreenDark
)

// ─── Screen ──────────────────────────────────────────────────────────────────
@Composable
fun StatScreen(
    seedViewModel: SeedInventoryViewModel = viewModel()
) {
    val seeds      by seedViewModel.seeds.collectAsState()
    val totalSeeds by seedViewModel.totalSeeds.collectAsState()
    val avgGerm    by seedViewModel.averageGerminationTime.collectAsState()

    // Données fictives légumes pour la démo (remplacer par ton VegetableViewModel)
    val sampleVegetables = remember { sampleVegetableData() }

    Scaffold(
        containerColor = Color(0xFFF1F8E9)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            // ── En-tête ──
            item {
                Text(
                    text = "📊 Statistiques",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GreenDark
                )
                Text(
                    text = "Vue d'ensemble de votre jardin",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // ── KPI Cards ──
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        label  = "Variétés",
                        value  = "${seeds.size}",
                        emoji  = "🌱",
                        color  = GreenLight,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        label  = "Graines",
                        value  = "$totalSeeds",
                        emoji  = "🫘",
                        color  = OrangeChart,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        label  = "Légumes",
                        value  = "${sampleVegetables.size}",
                        emoji  = "🥦",
                        color  = BlueChart,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Donut : répartition des quantités par graine ──
            item {
                ChartCard(title = "🫘 Répartition des graines") {
                    if (seeds.isNotEmpty()) {
                        DonutChart(
                            slices = seeds.map { it.name to it.quantity.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )
                    } else {
                        EmptyState("Aucune graine dans l'inventaire")
                    }
                }
            }

            // ── Barres : temps de germination des graines ──
            item {
                ChartCard(title = "⏱️ Temps de germination (jours)") {
                    if (seeds.isNotEmpty()) {
                        BarChart(
                            data   = seeds.map { it.name to it.germinationTime.toFloat() },
                            color  = GreenDark,
                            unit   = "j",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    } else {
                        EmptyState("Aucune donnée")
                    }
                }
            }

            // ── Barres : besoins en eau des légumes ──
            item {
                ChartCard(title = "💧 Besoins en eau (mm)") {
                    BarChart(
                        data   = sampleVegetables.map { it.name to (it.waterNeedsMm?.toFloat() ?: 0f) },
                        color  = BlueChart,
                        unit   = "mm",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }

            // ── Ligne : fenêtres de semis par légume ──
            item {
                ChartCard(title = "📅 Durée de semis par légume (mois)") {
                    val sowingData = sampleVegetables.map { veg ->
                        val months = veg.sowingEnd.monthValue - veg.sowingStart.monthValue + 1
                        veg.name to months.toFloat().coerceAtLeast(1f)
                    }
                    BarChart(
                        data   = sowingData,
                        color  = TealChart,
                        unit   = "m",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }

            // ── Légende expiry ──
            item {
                ChartCard(title = "🗓️ Graines expirant bientôt") {
                    ExpiryList(seeds = seeds)
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

// ─── KPI Card ────────────────────────────────────────────────────────────────
@Composable
private fun KpiCard(
    label: String,
    value: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier         = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 22.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

// ─── Chart Card wrapper ───────────────────────────────────────────────────────
@Composable
private fun ChartCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GreenDark)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

// ─── Donut Chart ─────────────────────────────────────────────────────────────
@Composable
private fun DonutChart(
    slices: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    val total = slices.sumOf { it.second.toDouble() }.toFloat()
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(900, easing = FastOutSlowInEasing))
    }
    val progress by animProgress.asState()

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            var startAngle = -90f
            slices.forEachIndexed { i, (_, value) ->
                val sweep = (value / total) * 360f * progress
                drawArc(
                    color       = chartColors[i % chartColors.size],
                    startAngle  = startAngle,
                    sweepAngle  = sweep,
                    useCenter   = false,
                    style       = Stroke(width = 48f, cap = StrokeCap.Butt),
                    size        = Size(
                        min(size.width, size.height) - 50f,
                        min(size.width, size.height) - 50f
                    ),
                    topLeft     = Offset(
                        (size.width  - (min(size.width, size.height) - 50f)) / 2f,
                        (size.height - (min(size.width, size.height) - 50f)) / 2f
                    )
                )
                startAngle += sweep
            }
        }

        // Légende
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            slices.take(6).forEachIndexed { i, (name, value) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(chartColors[i % chartColors.size])
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text     = "$name (${value.toInt()})",
                        fontSize = 11.sp,
                        color    = Color.DarkGray,
                        maxLines = 1
                    )
                }
            }
            if (slices.size > 6) {
                Text("+${slices.size - 6} autres", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

// ─── Bar Chart ────────────────────────────────────────────────────────────────
@Composable
private fun BarChart(
    data: List<Pair<String, Float>>,
    color: Color,
    unit: String,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
    }
    val progress by animProgress.asState()
    val maxVal = data.maxOfOrNull { it.second } ?: 1f

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val barCount  = data.size
            val spacing   = 12f
            val barWidth  = (size.width - spacing * (barCount + 1)) / barCount
            val maxHeight = size.height - 24f

            data.forEachIndexed { i, (_, value) ->
                val barHeight = (value / maxVal) * maxHeight * progress
                val left      = spacing + i * (barWidth + spacing)
                val top       = size.height - barHeight - 20f

                drawRoundRect(
                    color        = color.copy(alpha = 0.85f),
                    topLeft      = Offset(left, top),
                    size         = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
                )
            }
        }

        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { (name, value) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text     = "${value.toInt()}$unit",
                        fontSize = 9.sp,
                        color    = color,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text     = name.take(5),
                        fontSize = 9.sp,
                        color    = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ─── Expiry list ─────────────────────────────────────────────────────────────
@Composable
private fun ExpiryList(seeds: List<Seed>) {
    if (seeds.isEmpty()) {
        EmptyState("Aucune graine")
        return
    }
    // Tri par date d'expiration (string ISO → comparaison lexicographique fonctionne)
    val sorted = seeds.sortedBy { it.expiryDate }.take(5)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        sorted.forEach { seed ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F8E9))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(seed.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(
                    text     = seed.expiryDate.take(10),
                    fontSize = 12.sp,
                    color    = OrangeChart,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ─── Empty state ─────────────────────────────────────────────────────────────
@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = Color.Gray, fontSize = 13.sp)
    }
}

// ─── Sample vegetable data (remplacer par le vrai ViewModel) ─────────────────
private fun sampleVegetableData(): List<Vegetable> {
    val base = java.time.LocalDate.now()
    return listOf(
        Vegetable(1, "Tomate",   10, "Solanum lycopersicum",
            base.withMonth(3), base.withMonth(5),
            base.withMonth(7), base.withMonth(10), waterNeedsMm = 45.0),
        Vegetable(2, "Carotte",  14, "Daucus carota",
            base.withMonth(3), base.withMonth(7),
            base.withMonth(7), base.withMonth(10), waterNeedsMm = 25.0),
        Vegetable(3, "Courgette", 7, "Cucurbita pepo",
            base.withMonth(4), base.withMonth(6),
            base.withMonth(7), base.withMonth(9),  waterNeedsMm = 60.0),
        Vegetable(4, "Laitue",    5, "Lactuca sativa",
            base.withMonth(3), base.withMonth(8),
            base.withMonth(5), base.withMonth(10), waterNeedsMm = 30.0),
        Vegetable(5, "Poivron",  12, "Capsicum annuum",
            base.withMonth(3), base.withMonth(4),
            base.withMonth(8), base.withMonth(10), waterNeedsMm = 40.0),
        Vegetable(6, "Concombre", 8, "Cucumis sativus",
            base.withMonth(4), base.withMonth(5),
            base.withMonth(7), base.withMonth(9),  waterNeedsMm = 55.0),
    )
}