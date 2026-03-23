package app.jardinageons.presentation.features.garden.model

data class GardenCanvasModel(
    val width: Float,
    val height: Float,
    val elements: List<GardenElement>
)

data class GardenPoint(
    val x: Float,
    val y: Float
)

sealed interface GardenElement {
    data class Parcelle(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val textureKey: String?
    ) : GardenElement

    data class Ellipse(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val textureKey: String?
    ) : GardenElement

    data class Trait(
        val points: List<GardenPoint>,
        val width: Float,
        val textureKey: String?
    ) : GardenElement

    data class Vegetable(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val name: String?,
        val imageUrl: String?
    ) : GardenElement
}
