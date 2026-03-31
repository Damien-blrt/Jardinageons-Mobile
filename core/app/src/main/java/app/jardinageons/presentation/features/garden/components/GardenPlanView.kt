package app.jardinageons.presentation.features.garden.components
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.jardinageons.R
import app.jardinageons.presentation.features.garden.model.GardenCanvasModel
import app.jardinageons.presentation.features.garden.model.GardenElement
import coil.compose.AsyncImage
import java.text.Normalizer
import kotlin.math.min


private const val WEB_BASE_URL =
    "https://codefirst.iut.uca.fr/kubernetes/iut-inf63-projets-etudiants-jardinageons/jardinageons"
private const val ROTATE_CANVAS_FOR_PORTRAIT = true

private val GardenBackgroundColor = Color(0xFFEFF4E6)
private val GardenFillColor = Color(0xFFB5C99A)
private val GardenBorderColor = Color(0xFF8CA06E)
private val GardenPathColor = Color(0xFF6C8D43)


// La partie affichage du jardin a été entierement généré a l'IA


@Composable
fun GardenPlanView(
    canvasModel: GardenCanvasModel,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    contentPadding: Dp = 8.dp,
    backgroundColor: Color = GardenBackgroundColor
) {
    val groupedElements = remember(canvasModel.elements) {
        canvasModel.groupElements()
    }
    val textureBrushes = rememberTextureBrushes()
    val density = LocalDensity.current

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(contentPadding)
        ) {
            val containerWidthPx = with(density) { maxWidth.toPx() }
            val containerHeightPx = with(density) { maxHeight.toPx() }
            val viewport = remember(
                containerWidthPx,
                containerHeightPx,
                canvasModel.width,
                canvasModel.height
            ) {
                buildGardenViewport(
                    containerWidthPx = containerWidthPx,
                    containerHeightPx = containerHeightPx,
                    canvasModel = canvasModel
                )
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawGardenShapes(
                    groupedElements = groupedElements,
                    viewport = viewport,
                    canvasHeight = canvasModel.height,
                    textureBrushes = textureBrushes
                )
            }

            GardenVegetableOverlay(
                vegetables = groupedElements.vegetables,
                viewport = viewport,
                canvasHeight = canvasModel.height
            )
        }
    }
}

private data class GardenGroupedElements(
    val parcelles: List<GardenElement.Parcelle>,
    val ellipses: List<GardenElement.Ellipse>,
    val traits: List<GardenElement.Trait>,
    val vegetables: List<GardenElement.Vegetable>
)

private data class GardenViewport(
    val scale: Float,
    val offsetXPx: Float,
    val offsetYPx: Float,
    val drawingWidthPx: Float,
    val drawingHeightPx: Float
)

private data class DisplayRect(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    fun topLeft(scale: Float): Offset = Offset(x * scale, y * scale)
    fun size(scale: Float): Size = Size(width * scale, height * scale)
}

private fun GardenCanvasModel.groupElements(): GardenGroupedElements {
    val parcelles = mutableListOf<GardenElement.Parcelle>()
    val ellipses = mutableListOf<GardenElement.Ellipse>()
    val traits = mutableListOf<GardenElement.Trait>()
    val vegetables = mutableListOf<GardenElement.Vegetable>()

    elements.forEach { element ->
        when (element) {
            is GardenElement.Parcelle -> parcelles += element
            is GardenElement.Ellipse -> ellipses += element
            is GardenElement.Trait -> traits += element
            is GardenElement.Vegetable -> vegetables += element
        }
    }

    return GardenGroupedElements(
        parcelles = parcelles,
        ellipses = ellipses,
        traits = traits,
        vegetables = vegetables
    )
}

private fun buildGardenViewport(
    containerWidthPx: Float,
    containerHeightPx: Float,
    canvasModel: GardenCanvasModel
): GardenViewport {
    val mapWidth = if (ROTATE_CANVAS_FOR_PORTRAIT) canvasModel.height else canvasModel.width
    val mapHeight = if (ROTATE_CANVAS_FOR_PORTRAIT) canvasModel.width else canvasModel.height
    val scale = min(
        containerWidthPx / mapWidth,
        containerHeightPx / mapHeight
    ).coerceAtLeast(0.001f)

    val drawingWidthPx = mapWidth * scale
    val drawingHeightPx = mapHeight * scale

    return GardenViewport(
        scale = scale,
        offsetXPx = ((containerWidthPx - drawingWidthPx) / 2f).coerceAtLeast(0f),
        offsetYPx = ((containerHeightPx - drawingHeightPx) / 2f).coerceAtLeast(0f),
        drawingWidthPx = drawingWidthPx,
        drawingHeightPx = drawingHeightPx
    )
}

private fun DrawScope.drawGardenShapes(
    groupedElements: GardenGroupedElements,
    viewport: GardenViewport,
    canvasHeight: Float,
    textureBrushes: Map<String, ShaderBrush>
) {
    withTransform({
        translate(left = viewport.offsetXPx, top = viewport.offsetYPx)
    }) {
        groupedElements.parcelles.forEach { parcelle ->
            drawRectElement(
                displayRect = parcelle.toDisplayRect(canvasHeight),
                textureKey = parcelle.textureKey,
                scale = viewport.scale,
                textureBrushes = textureBrushes
            )
        }

        groupedElements.ellipses.forEach { ellipse ->
            drawOvalElement(
                displayRect = ellipse.toDisplayRect(canvasHeight),
                textureKey = ellipse.textureKey,
                scale = viewport.scale,
                textureBrushes = textureBrushes
            )
        }

        groupedElements.traits.forEach { trait ->
            drawTraitElement(
                trait = trait,
                scale = viewport.scale,
                canvasHeight = canvasHeight,
                textureBrushes = textureBrushes
            )
        }

        drawRect(
            color = GardenBorderColor,
            size = Size(viewport.drawingWidthPx, viewport.drawingHeightPx),
            style = Stroke(width = 2f)
        )
    }
}

private fun DrawScope.drawRectElement(
    displayRect: DisplayRect,
    textureKey: String?,
    scale: Float,
    textureBrushes: Map<String, ShaderBrush>
) {
    val topLeft = displayRect.topLeft(scale)
    val size = displayRect.size(scale)
    val brush = textureKey?.let(textureBrushes::get)

    if (brush != null) {
        drawRect(brush = brush, topLeft = topLeft, size = size)
    } else {
        drawRect(color = GardenFillColor, topLeft = topLeft, size = size)
    }

    drawRect(
        color = GardenBorderColor,
        topLeft = topLeft,
        size = size,
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawOvalElement(
    displayRect: DisplayRect,
    textureKey: String?,
    scale: Float,
    textureBrushes: Map<String, ShaderBrush>
) {
    val topLeft = displayRect.topLeft(scale)
    val size = displayRect.size(scale)
    val brush = textureKey?.let(textureBrushes::get)

    if (brush != null) {
        drawOval(brush = brush, topLeft = topLeft, size = size)
    } else {
        drawOval(color = GardenFillColor, topLeft = topLeft, size = size)
    }

    drawOval(
        color = GardenBorderColor,
        topLeft = topLeft,
        size = size,
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawTraitElement(
    trait: GardenElement.Trait,
    scale: Float,
    canvasHeight: Float,
    textureBrushes: Map<String, ShaderBrush>
) {
    val path = Path().apply {
        trait.points.forEachIndexed { index, point ->
            val displayPoint = toDisplayPoint(
                x = point.x,
                y = point.y,
                canvasHeight = canvasHeight
            )

            if (index == 0) {
                moveTo(displayPoint.x * scale, displayPoint.y * scale)
            } else {
                lineTo(displayPoint.x * scale, displayPoint.y * scale)
            }
        }
    }

    drawPath(
        path = path,
        brush = trait.textureKey?.let(textureBrushes::get) ?: SolidColor(GardenPathColor),
        style = Stroke(width = (trait.width * scale).coerceAtLeast(1f))
    )
}

@Composable
private fun GardenVegetableOverlay(
    vegetables: List<GardenElement.Vegetable>,
    viewport: GardenViewport,
    canvasHeight: Float
) {
    val density = LocalDensity.current

    vegetables.forEach { vegetable ->
        val displayRect = vegetable.toDisplayRect(canvasHeight)
        val xDp = with(density) {
            (viewport.offsetXPx + displayRect.x * viewport.scale).toDp()
        }
        val yDp = with(density) {
            (viewport.offsetYPx + displayRect.y * viewport.scale).toDp()
        }
        val widthDp = with(density) { (displayRect.width * viewport.scale).toDp() }
        val heightDp = with(density) { (displayRect.height * viewport.scale).toDp() }

        AsyncImage(
            model = resolveVegetableImage(vegetable.imageUrl, vegetable.name),
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

private fun GardenElement.Parcelle.toDisplayRect(canvasHeight: Float): DisplayRect =
    toDisplayRect(x = x, y = y, width = width, height = height, canvasHeight = canvasHeight)

private fun GardenElement.Ellipse.toDisplayRect(canvasHeight: Float): DisplayRect =
    toDisplayRect(x = x, y = y, width = width, height = height, canvasHeight = canvasHeight)

private fun GardenElement.Vegetable.toDisplayRect(canvasHeight: Float): DisplayRect =
    toDisplayRect(x = x, y = y, width = width, height = height, canvasHeight = canvasHeight)

private fun toDisplayRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    canvasHeight: Float
): DisplayRect {
    if (!ROTATE_CANVAS_FOR_PORTRAIT) {
        return DisplayRect(x = x, y = y, width = width, height = height)
    }

    return DisplayRect(
        x = canvasHeight - (y + height),
        y = x,
        width = height,
        height = width
    )
}

private fun toDisplayPoint(
    x: Float,
    y: Float,
    canvasHeight: Float
): Offset {
    if (!ROTATE_CANVAS_FOR_PORTRAIT) {
        return Offset(x, y)
    }

    return Offset(
        x = canvasHeight - y,
        y = x
    )
}

private fun resolveVegetableImage(imageUrl: String?, vegetableName: String?): Any {
    resolveLocalVegetableDrawable(imageUrl, vegetableName)?.let { return it }

    val raw = imageUrl?.trim().orEmpty()
    if (raw.isNotEmpty()) {
        return toAbsoluteWebUrl(raw)
    }

    val name = vegetableName?.trim().orEmpty()
    if (name.isEmpty()) return R.drawable.vegetable_default

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

private val LOCAL_VEGETABLE_BY_KEY: Map<String, Int> = mapOf(
    "aubergine" to R.drawable.veg_aubergine,
    "betterave" to R.drawable.veg_betterave,
    "carotte" to R.drawable.veg_carotte,
    "carottes" to R.drawable.veg_carotte,
    "chou_fleur" to R.drawable.veg_chou_fleur,
    "courgette" to R.drawable.veg_courgette,
    "epinard" to R.drawable.veg_epinard,
    "laitue" to R.drawable.veg_laitue,
    "oignon" to R.drawable.veg_oignon,
    "oignons" to R.drawable.veg_oignon,
    "patate" to R.drawable.veg_patate,
    "poireau" to R.drawable.veg_poireau,
    "poivron" to R.drawable.veg_poivron,
    "radis" to R.drawable.vegetable_default,
    "salade" to R.drawable.veg_salade,
    "tomate" to R.drawable.veg_tomate,
    "tomates" to R.drawable.veg_tomate
)

private fun resolveLocalVegetableDrawable(imageUrl: String?, vegetableName: String?): Int? {
    val nameKey = normalizeImageKey(vegetableName)
    if (nameKey.isNotEmpty()) {
        LOCAL_VEGETABLE_BY_KEY[nameKey]?.let { return it }
    }

    val filename = imageUrl
        ?.substringAfterLast('/')
        ?.substringBefore('?')
        ?.substringBefore('#')
        ?.substringBeforeLast('.')
        .orEmpty()
    if (filename.isNotEmpty()) {
        val fileKey = normalizeImageKey(filename)
        LOCAL_VEGETABLE_BY_KEY[fileKey]?.let { return it }
    }

    return null
}

private fun normalizeImageKey(raw: String?): String {
    if (raw.isNullOrBlank()) return ""

    val noAccents = Normalizer.normalize(raw, Normalizer.Form.NFD)
        .replace("\\p{M}+".toRegex(), "")

    return noAccents
        .lowercase()
        .replace("[^a-z0-9]+".toRegex(), "_")
        .trim('_')
}
