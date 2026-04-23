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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LineChart(
    data: List<Pair<String, Float>>,
    gradient: List<Color>,
    unit: String,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(900, easing = FastOutSlowInEasing))
    }
    val progress  by animProgress.asState()
    val maxVal    = data.maxOfOrNull { it.second }?.coerceAtLeast(1f) ?: 1f
    val lineColor = gradient.first()

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            val count     = data.size
            val stepX     = size.width / (count - 1).toFloat()
            val maxHeight = size.height - 16f

            val points = data.mapIndexed { i, (_, value) ->
                Offset(
                    x = i * stepX,
                    y = size.height - (value / maxVal) * maxHeight * progress
                )
            }

            val fillPath = Path().apply {
                moveTo(points.first().x, size.height)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, size.height)
                close()
            }
            drawPath(
                path  = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent),
                    startY = 0f,
                    endY   = size.height
                )
            )

            for (i in 0 until points.size - 1) {
                drawLine(
                    color       = lineColor,
                    start       = points[i],
                    end         = points[i + 1],
                    strokeWidth = 4f,
                    cap         = StrokeCap.Round
                )
            }

            points.forEach { point ->
                drawCircle(color = Color.White, radius = 7f, center = point)
                drawCircle(color = lineColor,   radius = 5f, center = point)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { (name, value) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = if (value > 0f) "${value.toInt()}$unit" else "",
                        fontSize   = 8.sp,
                        color      = lineColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text     = name,
                        fontSize = 8.sp,
                        color    = Color(0xFF8E8E93)
                    )
                }
            }
        }
    }
}