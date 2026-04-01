package app.jardinageons.presentation.features.statPage.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun BarChart(
    data: List<Pair<String, Float>>,
    gradient: List<Color>,
    unit: String,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
    }
    val progress by animProgress.asState()
    val maxVal   = data.maxOfOrNull { it.second } ?: 1f
    val barColor = gradient.first()

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
                    color        = barColor.copy(alpha = 0.15f),
                    topLeft      = Offset(left, 0f),
                    size         = Size(barWidth, size.height - 20f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f)
                )
                drawRoundRect(
                    color        = barColor,
                    topLeft      = Offset(left, top),
                    size         = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f)
                )
            }
        }

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
                        text       = "${value.toInt()}$unit",
                        fontSize   = 9.sp,
                        color      = barColor,
                        fontWeight = FontWeight.Bold,
                        textAlign  = TextAlign.Center
                    )
                    Text(
                        text      = name.take(5),
                        fontSize  = 9.sp,
                        color     = Color(0xFF8E8E93),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}