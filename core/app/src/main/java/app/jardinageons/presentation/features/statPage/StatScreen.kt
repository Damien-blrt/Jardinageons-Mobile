package app.jardinageons.presentation.features.statPage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.R
import app.jardinageons.data.models.Seed
import app.jardinageons.presentation.features.statPage.charts.BarChart
import app.jardinageons.presentation.features.statPage.charts.DonutChart
import app.jardinageons.presentation.features.statPage.charts.LineChart
import app.jardinageons.presentation.features.statPage.components.*

private val BackgroundColor = Color(0xFFF2F2F7)
private val TitleColor      = Color(0xFF1C1C1E)

private val GradientGreen  = listOf(Color(0xFF11998e), Color(0xFF38ef7d))
private val GradientOrange = listOf(Color(0xFFf7971e), Color(0xFFffd200))
private val GradientBlue   = listOf(Color(0xFF1976D2), Color(0xFF42A5F5))
private val GradientTeal   = listOf(Color(0xFF00897B), Color(0xFF4DB6AC))
private val GradientPurple = listOf(Color(0xFF7B1FA2), Color(0xFFBA68C8))

// ─── Utils ────────────────────────────────────────────────────────────────────

private fun seedsExpiryByMonth(
    seeds: List<Seed>,
    months: List<String>
): List<Pair<String, Float>> {
    val counts = IntArray(12) { 0 }
    seeds.forEach { seed ->
        try {
            val month = seed.expiryDate.substring(5, 7).toInt() - 1
            if (month in 0..11) counts[month]++
        } catch (e: Exception) { /* ignore */ }
    }
    return months.mapIndexed { i, name -> name to counts[i].toFloat() }
}

// TODO: décommenter quand HarvestRepository dispo
// private fun harvestsByMonth(harvests: List<Harvest>, months: List<String>): List<Pair<String, Float>> {
//     val counts = IntArray(12) { 0 }
//     harvests.forEach { harvest ->
//         try {
//             val month = harvest.date.substring(5, 7).toInt() - 1
//             if (month in 0..11) counts[month]++
//         } catch (e: Exception) { /* ignore */ }
//     }
//     return months.mapIndexed { i, name -> name to counts[i].toFloat() }
// }

// TODO: décommenter quand HarvestRepository dispo
// private fun harvestQuantities(harvests: List<Harvest>): List<Pair<String, Float>> {
//     return harvests.map { harvest ->
//         harvest.date.substring(0, 10) to harvest.quantity.toFloat()
//     }
// }

// ─── Screen ──────────────────────────────────────────────────────────────────

@Composable
fun StatScreen(
    viewModel: StatViewModel = viewModel()
) {
    val seeds      by viewModel.seeds.collectAsState()
    val totalSeeds by viewModel.totalSeeds.collectAsState()
    val isLoading  by viewModel.isLoading.collectAsState()
    val months     = stringArrayResource(R.array.stat_months).toList()

    // TODO: décommenter quand HarvestRepository dispo
    // val harvests by viewModel.harvests.collectAsState()

    Scaffold(containerColor = BackgroundColor) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF38ef7d))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {

                // ── En-tête ──
                item {
                    Text(
                        text = stringResource(R.string.stat_title),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TitleColor,
                        modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.stat_subtitle),
                        fontSize = 14.sp,
                        color = Color(0xFF8E8E93),
                        modifier = Modifier.padding(start = 20.dp, bottom = 20.dp)
                    )
                }

                // ── KPI Cards ──
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatKpiCard(stringResource(R.string.stat_kpi_varieties), "${seeds.size}",  GradientGreen,  Modifier.weight(1f))
                        StatKpiCard(stringResource(R.string.stat_kpi_seeds),     "$totalSeeds",    GradientOrange, Modifier.weight(1f))
                        StatKpiCard(stringResource(R.string.stat_kpi_vegetables),"-",              GradientBlue,   Modifier.weight(1f))
                        // TODO: vegetables.size quand VegetableRepository dispo
                    }
                }

                // ── Répartition des graines ──
                item { StatSectionTitle(stringResource(R.string.stat_section_seed_distribution)) }
                item {
                    StatChartCard {
                        if (seeds.isNotEmpty()) {
                            DonutChart(
                                slices = seeds.map { it.name to it.quantity.toFloat() },
                                modifier = Modifier.fillMaxWidth().height(220.dp)
                            )
                        } else {
                            StatEmptyState(stringResource(R.string.stat_empty_no_seeds))
                        }
                    }
                }

                // ── Temps de germination ──
                item { StatSectionTitle(stringResource(R.string.stat_section_germination)) }
                item {
                    StatChartCard {
                        if (seeds.isNotEmpty()) {
                            BarChart(
                                data     = seeds.map { it.name to it.germinationTime.toFloat() },
                                gradient = GradientGreen,
                                unit     = "j",
                                modifier = Modifier.fillMaxWidth().height(200.dp)
                            )
                        } else {
                            StatEmptyState(stringResource(R.string.stat_empty_no_data))
                        }
                    }
                }

                // ── Besoins en eau ──
                item { StatSectionTitle(stringResource(R.string.stat_section_water_needs)) }
                item {
                    StatChartCard {
                        // TODO: décommenter quand VegetableRepository dispo
                        // BarChart(
                        //     data     = vegetables.map { it.name to (it.waterNeedsMm?.toFloat() ?: 0f) },
                        //     gradient = GradientBlue,
                        //     unit     = "mm",
                        //     modifier = Modifier.fillMaxWidth().height(200.dp)
                        // )
                        StatEmptyState(stringResource(R.string.stat_empty_vegetable_todo))
                    }
                }

                // ── Durée de semis ──
                item { StatSectionTitle(stringResource(R.string.stat_section_sowing_duration)) }
                item {
                    StatChartCard {
                        // TODO: décommenter quand VegetableRepository dispo
                        // val sowingData = vegetables.map { veg ->
                        //     val months = veg.sowingEnd.monthValue - veg.sowingStart.monthValue + 1
                        //     veg.name to months.toFloat().coerceAtLeast(1f)
                        // }
                        // BarChart(
                        //     data     = sowingData,
                        //     gradient = GradientTeal,
                        //     unit     = "m",
                        //     modifier = Modifier.fillMaxWidth().height(200.dp)
                        // )
                        StatEmptyState(stringResource(R.string.stat_empty_vegetable_todo))
                    }
                }

                // ── Expirations par mois ──
                item { StatSectionTitle(stringResource(R.string.stat_section_expiry_by_month)) }
                item {
                    StatChartCard {
                        LineChart(
                            data     = seedsExpiryByMonth(seeds, months),
                            gradient = GradientOrange,
                            unit     = "",
                            modifier = Modifier.fillMaxWidth().height(200.dp)
                        )
                    }
                }

                // ── Récoltes par mois ──
                item { StatSectionTitle(stringResource(R.string.stat_section_harvests_by_month)) }
                item {
                    StatChartCard {
                        // TODO: décommenter quand HarvestRepository dispo
                        // LineChart(
                        //     data     = harvestsByMonth(harvests, months),
                        //     gradient = GradientTeal,
                        //     unit     = "",
                        //     modifier = Modifier.fillMaxWidth().height(200.dp)
                        // )
                        StatEmptyState(stringResource(R.string.stat_empty_harvest_todo))
                    }
                }

                // ── Quantités récoltées ──
                item { StatSectionTitle(stringResource(R.string.stat_section_harvest_quantities)) }
                item {
                    StatChartCard {
                        // TODO: décommenter quand HarvestRepository dispo
                        // BarChart(
                        //     data     = harvestQuantities(harvests),
                        //     gradient = GradientPurple,
                        //     unit     = "kg",
                        //     modifier = Modifier.fillMaxWidth().height(200.dp)
                        // )
                        StatEmptyState(stringResource(R.string.stat_empty_harvest_todo))
                    }
                }

                // ── Graines expirant bientôt ──
                item { StatSectionTitle(stringResource(R.string.stat_section_expiring_soon)) }
                item {
                    StatChartCard {
                        ExpiryList(seeds = seeds)
                    }
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}