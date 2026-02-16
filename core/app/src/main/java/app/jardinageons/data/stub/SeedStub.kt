package app.jardinageons.data.stub

import app.jardinageons.data.models.Seed
import kotlinx.serialization.Serializable

object SeedStub {
    private val tomate = Seed(
        id = 1,
        name = "tomatses de test",
        quantity = 3F,
        germinationTime = 21,
        description = "graine super",
        vegetableId = null,
        expiryDate = "21/08/2026"
    )
    private val poireau = Seed(
        id = 1,
        name = "poireau",
        quantity = 6F,
        germinationTime = 21,
        description = "graine pas super",
        vegetableId = null,
        expiryDate = "21/08/2027"
    )

    val seeds by lazy{
        listOf(
            tomate,
            poireau
        )
    }
}

