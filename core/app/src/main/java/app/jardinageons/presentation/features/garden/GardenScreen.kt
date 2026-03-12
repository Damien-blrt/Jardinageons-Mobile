package app.jardinageons.presentation.features.garden

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jardinageons.R
import app.jardinageons.presentation.features.garden.model.GardenCanvasModel
import app.jardinageons.presentation.features.garden.model.GardenElement
import coil.compose.AsyncImage
import kotlin.math.min

private const val WEB_BASE_URL =
    "https://codefirst.iut.uca.fr/kubernetes/iut-inf63-projets-etudiants-jardinageons/jardinageons"

@Composable
fun GardenScreen(
    viewModel: GardenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.gardens.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Aucun jardin disponible.")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = viewModel::loadGardens) {
                    Text("Réessayer")
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Mon potager",
            style = MaterialTheme.typography.titleLarge
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.gardens, key = { it.id }) { garden ->
                FilterChip(
                    selected = uiState.selectedGardenId == garden.id,
                    onClick = { viewModel.selectGarden(garden.id) },
                    label = { Text(garden.name) }
                )
            }
        }

        if (uiState.selectedCanvas == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEFF4E6)),
                contentAlignment = Alignment.Center
            ) {
                Text("Ce jardin ne contient pas encore de plan.")
            }
        } else {
            GardenPlanCanvas(
                canvasModel = uiState.selectedCanvas,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun GardenPlanCanvas(
    canvasModel: GardenCanvasModel,
    modifier: Modifier = Modifier
) {
    val textureBrushes = rememberTextureBrushes()
    val density = LocalDensity.current

    val parcelles = remember(canvasModel.elements) {
        canvasModel.elements.filterIsInstance<GardenElement.Parcelle>()
    }
    val ellipses = remember(canvasModel.elements) {
        canvasModel.elements.filterIsInstance<GardenElement.Ellipse>()
    }
    val traits = remember(canvasModel.elements) {
        canvasModel.elements.filterIsInstance<GardenElement.Trait>()
    }
    val vegetables = remember(canvasModel.elements) {
        canvasModel.elements.filterIsInstance<GardenElement.Vegetable>()
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFF4E6))
                .padding(8.dp)
        ) {
            val containerWidthPx = with(density) { maxWidth.toPx() }
            val containerHeightPx = with(density) { maxHeight.toPx() }
            val scale = remember(containerWidthPx, containerHeightPx, canvasModel.width, canvasModel.height) {
                min(
                    containerWidthPx / canvasModel.width,
                    containerHeightPx / canvasModel.height
                ).coerceAtLeast(0.001f)
            }

            val drawingWidthPx = canvasModel.width * scale
            val drawingHeightPx = canvasModel.height * scale
            val offsetXPx = ((containerWidthPx - drawingWidthPx) / 2f).coerceAtLeast(0f)
            val offsetYPx = ((containerHeightPx - drawingHeightPx) / 2f).coerceAtLeast(0f)

            Canvas(modifier = Modifier.fillMaxSize()) {
                val baseBrush = canvasModel.defaultTextureKey?.let(textureBrushes::get)

                withTransform({
                    translate(left = offsetXPx, top = offsetYPx)
                }) {
                    if (baseBrush != null) {
                        drawRect(
                            brush = baseBrush,
                            size = androidx.compose.ui.geometry.Size(drawingWidthPx, drawingHeightPx)
                        )
                    } else {
                        drawRect(
                            color = Color(0xFFF5F8EE),
                            size = androidx.compose.ui.geometry.Size(drawingWidthPx, drawingHeightPx)
                        )
                    }

                    parcelles.forEach { parcelle ->
                        val topLeft = androidx.compose.ui.geometry.Offset(
                            parcelle.x * scale,
                            parcelle.y * scale
                        )
                        val size = androidx.compose.ui.geometry.Size(
                            parcelle.width * scale,
                            parcelle.height * scale
                        )
                        val brush = parcelle.textureKey?.let(textureBrushes::get)
                        if (brush != null) {
                            drawRect(brush = brush, topLeft = topLeft, size = size)
                        } else {
                            drawRect(color = Color(0xFFB5C99A), topLeft = topLeft, size = size)
                        }
                        drawRect(
                            color = Color(0xFF8CA06E),
                            topLeft = topLeft,
                            size = size,
                            style = Stroke(width = 2f)
                        )
                    }

                    ellipses.forEach { ellipse ->
                        val topLeft = androidx.compose.ui.geometry.Offset(
                            ellipse.x * scale,
                            ellipse.y * scale
                        )
                        val size = androidx.compose.ui.geometry.Size(
                            ellipse.width * scale,
                            ellipse.height * scale
                        )
                        val brush = ellipse.textureKey?.let(textureBrushes::get)
                        if (brush != null) {
                            drawOval(brush = brush, topLeft = topLeft, size = size)
                        } else {
                            drawOval(color = Color(0xFFB5C99A), topLeft = topLeft, size = size)
                        }
                        drawOval(
                            color = Color(0xFF8CA06E),
                            topLeft = topLeft,
                            size = size,
                            style = Stroke(width = 2f)
                        )
                    }

                    traits.forEach { trait ->
                        val path = Path().apply {
                            trait.points.forEachIndexed { index, point ->
                                val x = point.x * scale
                                val y = point.y * scale
                                if (index == 0) moveTo(x, y) else lineTo(x, y)
                            }
                        }

                        val strokeBrush = trait.textureKey?.let(textureBrushes::get)
                            ?: Brush.linearGradient(listOf(Color(0xFF6C8D43), Color(0xFF6C8D43)))

                        drawPath(
                            path = path,
                            brush = strokeBrush,
                            style = Stroke(width = (trait.width * scale).coerceAtLeast(1f))
                        )
                    }

                    drawRect(
                        color = Color(0xFF8CA06E),
                        size = androidx.compose.ui.geometry.Size(drawingWidthPx, drawingHeightPx),
                        style = Stroke(width = 2f)
                    )
                }
            }

            vegetables.forEach { vegetable ->
                val imageModel = resolveVegetableImage(vegetable.imageUrl, vegetable.name)
                val xDp = with(density) { (offsetXPx + vegetable.x * scale).toDp() }
                val yDp = with(density) { (offsetYPx + vegetable.y * scale).toDp() }
                val widthDp = with(density) { (vegetable.width * scale).toDp() }
                val heightDp = with(density) { (vegetable.height * scale).toDp() }

                AsyncImage(
                    model = imageModel,
                    contentDescription = vegetable.name,
                    placeholder = painterResource(id = R.drawable.vegetable_default),
                    error = painterResource(id = R.drawable.vegetable_default),
                    fallback = painterResource(id = R.drawable.vegetable_default),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .offset(x = xDp, y = yDp)
                        .size(width = widthDp, height = heightDp)
                        .clip(RoundedCornerShape(6.dp))
                )
            }
        }
    }
}

@Composable
private fun rememberTextureBrushes(): Map<String, ShaderBrush> {
    val herbe = ImageBitmap.imageResource(id = R.drawable.texture_herbe)
    val terre = ImageBitmap.imageResource(id = R.drawable.texture_terre)
    val gravier = ImageBitmap.imageResource(id = R.drawable.texture_gravier)

    return remember(herbe, terre, gravier) {
        mapOf(
            "herbe" to ShaderBrush(ImageShader(herbe, TileMode.Repeated, TileMode.Repeated)),
            "gazon" to ShaderBrush(ImageShader(herbe, TileMode.Repeated, TileMode.Repeated)),
            "terre" to ShaderBrush(ImageShader(terre, TileMode.Repeated, TileMode.Repeated)),
            "gravier" to ShaderBrush(ImageShader(gravier, TileMode.Repeated, TileMode.Repeated)),
            "pierre" to ShaderBrush(ImageShader(gravier, TileMode.Repeated, TileMode.Repeated))
        )
    }
}

private fun resolveVegetableImage(imageUrl: String?, vegetableName: String?): String? {
    val raw = imageUrl?.trim().orEmpty()
    if (raw.isNotEmpty()) {
        return toAbsoluteWebUrl(raw)
    }

    val name = vegetableName?.trim().orEmpty()
    if (name.isEmpty()) return null

    return "$WEB_BASE_URL/image/${Uri.encode(name)}.png"
}

private fun toAbsoluteWebUrl(raw: String): String {
    return when {
        raw.startsWith("https://") || raw.startsWith("http://") -> raw
        raw.startsWith("//") -> "https:$raw"
        raw.startsWith("/") -> "$WEB_BASE_URL$raw"
        else -> "$WEB_BASE_URL/$raw"
    }
}
