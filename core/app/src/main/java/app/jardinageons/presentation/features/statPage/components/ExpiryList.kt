package app.jardinageons.presentation.features.statPage.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.jardinageons.R
import app.jardinageons.data.models.Seed

private val TitleColor = Color(0xFF1C1C1E)

@Composable
fun ExpiryList(seeds: List<Seed>) {
    if (seeds.isEmpty()) {
        StatEmptyState(stringResource(R.string.stat_empty_no_seeds_short))
        return
    }

    val today = java.time.LocalDate.now()
    val soon = seeds.filter { seed ->
        try {
            val date = java.time.LocalDate.parse(
                seed.expiryDate.substring(0, 10),
                java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
            )
            val diff = java.time.temporal.ChronoUnit.DAYS.between(today, date)
            diff in -10..10
        } catch (e: Exception) { false }
    }.sortedBy { it.expiryDate }

    if (soon.isEmpty()) {
        StatEmptyState(stringResource(R.string.stat_empty_no_expiry))
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        soon.forEach { seed ->
            val date = java.time.LocalDate.parse(
                seed.expiryDate.substring(0, 10),
                java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
            )
            val diff = java.time.temporal.ChronoUnit.DAYS.between(today, date)
            val (label, color) = when {
                diff < 0   -> stringResource(R.string.stat_expiry_past,   -diff) to Color(0xFFec008c)
                diff == 0L -> stringResource(R.string.stat_expiry_today)         to Color(0xFFf7971e)
                else       -> stringResource(R.string.stat_expiry_future,  diff) to Color(0xFF38ef7d)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF2F2F7))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    seed.name,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color      = TitleColor
                )
                Text(
                    text       = label,
                    fontSize   = 12.sp,
                    color      = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}