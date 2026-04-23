package app.jardinageons.presentation.components

import android.graphics.PathMeasure
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp


/*
Ce code est grandement inspirée d'une IA et adaptée à notre projet
*/
@Composable
fun AnimatedPlantLoader(
    modifier: Modifier = Modifier,
    plantColor: Color = Color(0xFF4CAF50)
) {

    // Animation infinie
    val infiniteTransition = rememberInfiniteTransition(label = "plant")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = EaseInOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Canvas(modifier = Modifier.size(140.dp)) {

            // tige
            val fullPath = Path().apply {
                moveTo(center.x, size.height)
                quadraticBezierTo(
                    center.x - 60f,
                    size.height / 2,
                    center.x,
                    40f
                )
            }

            val pathMeasure = PathMeasure(
                fullPath.asAndroidPath(),
                false
            )

            val segmentPath = Path()

            pathMeasure.getSegment(
                0f,
                pathMeasure.length * progress,
                segmentPath.asAndroidPath(),
                true
            )

            drawPath(
                path = segmentPath,
                color = plantColor,
                style = Stroke(
                    width = 12f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Feuilles
            if (progress > 0.7f) {

                val leafProgress = (progress - 0.7f) / 0.3f
                val leafSize = 28f * leafProgress

                val topY = 40f

                rotate(-30f, pivot = center.copy(y = topY)) {
                    drawOval(
                        color = plantColor,
                        topLeft = center.copy(
                            x = center.x - (leafSize+50),
                            y = topY-20
                        ),
                        size = Size(leafSize, leafSize * 1.5f)
                    )
                }

                rotate(30f, pivot = center.copy(y = topY)) {
                    drawOval(
                        color = plantColor,
                        topLeft = center.copy(
                            x = center.x+50,
                            y = topY-20
                        ),
                        size = Size(leafSize, leafSize * 1.5f)
                    )
                }
            }
        }
    }
}