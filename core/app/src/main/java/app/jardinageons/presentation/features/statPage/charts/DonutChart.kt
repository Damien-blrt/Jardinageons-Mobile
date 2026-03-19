package app.jardinageons.presentation.features.statPage.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import kotlin.math.min

private val chartColors = listOf(
    Color(0xFF38ef7d), Color(0xFFfc6767), Color(0xFF42A5F5),
    Color(0xFF4DB6AC), Color(0xFFBA68C8), Color(0xFFFFD54F),
    Color(0xFF11998e), Color(0xFFec008c)
)

@Composable
fun DonutChart(
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
                    color      = chartColors[i % chartColors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter  = false,
                    style      = Stroke(width = 52f, cap = StrokeCap.Round),
                    size       = Size(
                        min(size.width, size.height) - 60f,
                        min(size.width, size.height) - 60f
                    ),
                    topLeft    = Offset(
                        (size.width  - (min(size.width, size.height) - 60f)) / 2f,
                        (size.height - (min(size.width, size.height) - 60f)) / 2f
                    )
                )
                startAngle += sweep
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            slices.take(6).forEachIndexed { i, (name, value) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(chartColors[i % chartColors.size])
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text     = "$name (${value.toInt()})",
                        fontSize = 11.sp,
                        color    = Color(0xFF3A3A3C),
                        maxLines = 1
                    )
                }
            }
            if (slices.size > 6) {
                Text("+${slices.size - 6} autres", fontSize = 11.sp, color = Color(0xFF8E8E93))
            }
        }
    }
}