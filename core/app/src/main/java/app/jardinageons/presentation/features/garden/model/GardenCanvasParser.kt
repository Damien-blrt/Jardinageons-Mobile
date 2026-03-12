package app.jardinageons.presentation.features.garden.model

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

private const val DEFAULT_CANVAS_WIDTH = 900f
private const val DEFAULT_CANVAS_HEIGHT = 650f
private val CONTROL_TYPES = setOf("delete-parcelle", "resize-parcelle")

fun parseGardenCanvasModel(rawJson: String?): GardenCanvasModel? {
    if (rawJson.isNullOrBlank()) return null

    return runCatching {
        val root = JsonParser().parse(rawJson).asJsonObject
        val canvasObject = root.getAsObject("canvas")

        val width = (canvasObject?.getAsFloatOrNull("width") ?: DEFAULT_CANVAS_WIDTH)
            .takeIf { it > 0f }
            ?: DEFAULT_CANVAS_WIDTH
        val height = (canvasObject?.getAsFloatOrNull("height") ?: DEFAULT_CANVAS_HEIGHT)
            .takeIf { it > 0f }
            ?: DEFAULT_CANVAS_HEIGHT
        val defaultTexture = root.getAsStringOrNull("texture")

        val elements = buildList {
            root.getAsArray("elements")?.forEach { element ->
                val obj = element.asJsonObjectOrNull() ?: return@forEach
                parseGardenElement(obj, defaultTexture)?.let(::add)
            }
        }

        GardenCanvasModel(
            width = width,
            height = height,
            defaultTextureKey = defaultTexture,
            elements = elements
        )
    }.getOrNull()
}

private fun parseGardenElement(
    obj: JsonObject,
    defaultTexture: String?
): GardenElement? {
    val type = obj.getAsStringOrNull("type") ?: return null

    if (type in CONTROL_TYPES) return null

    return when (type) {
        "parcelle" -> {
            GardenElement.Parcelle(
                x = obj.getAsFloatOrNull("x") ?: return null,
                y = obj.getAsFloatOrNull("y") ?: return null,
                width = obj.getAsFloatOrNull("width") ?: return null,
                height = obj.getAsFloatOrNull("height") ?: return null,
                textureKey = obj.getAsStringOrNull("textureKey") ?: defaultTexture
            )
        }

        "ellipse" -> {
            GardenElement.Ellipse(
                x = obj.getAsFloatOrNull("x") ?: return null,
                y = obj.getAsFloatOrNull("y") ?: return null,
                width = obj.getAsFloatOrNull("width") ?: return null,
                height = obj.getAsFloatOrNull("height") ?: return null,
                textureKey = obj.getAsStringOrNull("textureKey") ?: defaultTexture
            )
        }

        "trait" -> {
            val points = obj.getAsArray("points")
                ?.mapNotNull { pointElement ->
                    val point = pointElement.asJsonObjectOrNull() ?: return@mapNotNull null
                    val px = point.getAsFloatOrNull("x") ?: return@mapNotNull null
                    val py = point.getAsFloatOrNull("y") ?: return@mapNotNull null
                    GardenPoint(px, py)
                }
                .orEmpty()

            if (points.isEmpty()) return null

            GardenElement.Trait(
                points = points,
                width = obj.getAsFloatOrNull("width") ?: 4f,
                textureKey = obj.getAsStringOrNull("textureKey") ?: defaultTexture
            )
        }

        "vegetable" -> {
            GardenElement.Vegetable(
                x = obj.getAsFloatOrNull("x") ?: return null,
                y = obj.getAsFloatOrNull("y") ?: return null,
                width = obj.getAsFloatOrNull("width") ?: return null,
                height = obj.getAsFloatOrNull("height") ?: return null,
                name = obj.getAsStringOrNull("name"),
                imageUrl = obj.getAsStringOrNull("imageUrl")
            )
        }

        else -> null
    }
}

private fun JsonObject.getAsObject(key: String): JsonObject? =
    runCatching { get(key)?.asJsonObject }.getOrNull()

private fun JsonObject.getAsArray(key: String) =
    runCatching { get(key)?.asJsonArray }.getOrNull()

private fun JsonObject.getAsStringOrNull(key: String): String? =
    runCatching { get(key)?.takeIf { !it.isJsonNull }?.asString }.getOrNull()

private fun JsonObject.getAsFloatOrNull(key: String): Float? =
    runCatching { get(key)?.takeIf { !it.isJsonNull }?.asFloat }.getOrNull()

private fun JsonElement.asJsonObjectOrNull(): JsonObject? =
    runCatching { asJsonObject }.getOrNull()
