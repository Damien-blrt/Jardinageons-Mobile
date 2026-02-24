package app.jardinageons.data.stub

import app.jardinageons.data.models.Seed

object SeedStub {
    private val tomate = Seed(
        id = 1,
        name = "tomatses de test",
        quantity = 3,
        germinationTime = 21,
        description = "graine super",
        vegetableId = null,
        expiryDate = "21/08/2026"
    )
    private val poireau = Seed(
        id = 1,
        name = "poireau",
        quantity = 6,
        germinationTime = 21,
        description = "graine pas super",
        vegetableId = null,
        expiryDate = "21/08/2027"
    )

    val seeds by lazy {
        listOf(
            tomate,
            poireau
        )
    }
}

