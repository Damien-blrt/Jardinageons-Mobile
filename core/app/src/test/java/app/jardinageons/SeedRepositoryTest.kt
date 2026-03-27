package app.jardinageons

import app.jardinageons.data.dao.SeedDao
import app.jardinageons.data.entities.SeedEntity
import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.models.Seed
import app.jardinageons.data.repositories.SeedRepository
import app.jardinageons.data.services.ISeedService
import app.jardinageons.presentation.features.seedInventory.SeedRequest
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SeedRepositoryTest {

    private lateinit var service: ISeedService
    private lateinit var dao: SeedDao
    private lateinit var repository: SeedRepository

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun fakeSeed(
        id: Long = 1L,
        name: String = "Tomate",
        quantity: Int = 10,
        germinationTime: Int = 7,
        description: String = "Description",
        vegetableId: Long? = null,
        expiryDate: String = "2026-12-31"
    ) = Seed(id, name, quantity, germinationTime, description, vegetableId, expiryDate)

    private fun fakeSeedEntity(
        id: Long = 1L,
        name: String = "Tomate",
        quantity: Int = 10,
        germinationTime: Int = 7,
        description: String = "Description",
        vegetableId: Long? = null,
        expiryDate: String = "2026-12-31"
    ) = SeedEntity(id, name, quantity, germinationTime, description, vegetableId, expiryDate)

    private fun fakePagedResponse(seeds: List<Seed> = listOf(fakeSeed())) =
        PagedResponse(totalCount = seeds.size, pageIndex = 0, countPerPage = 10, items = seeds)

    private fun fakeSeedRequest() = SeedRequest(
        name = "Carotte",
        quantity = 5,
        germinationTime = 10,
        description = "Une carotte",
        vegetableId = 0,
        expiryDate = "2027-01-01"
    )

    // ── Setup ──────────────────────────────────────────────────────────────────

    @Before
    fun setup() {
        service = mockk()
        dao = mockk()
        repository = SeedRepository(service, dao)
    }

    // ── getSeedsFlow ───────────────────────────────────────────────────────────

    @Test
    fun `getSeedsFlow maps entities to Seed models`() = runTest {
        val entity = fakeSeedEntity(id = 1L, name = "Tomate")
        coEvery { dao.loadSeeds() } returns flowOf(listOf(entity))

        val result = repository.getSeedsFlow().first()

        assertEquals(1, result.size)
        assertEquals("Tomate", result[0].name)
        assertEquals(1L, result[0].id)
    }

    @Test
    fun `getSeedsFlow returns empty list when dao is empty`() = runTest {
        coEvery { dao.loadSeeds() } returns flowOf(emptyList())

        val result = repository.getSeedsFlow().first()

        assertTrue(result.isEmpty())
    }



    @Test
    fun `getSeedsFlow maps multiple entities correctly`() = runTest {
        val entities = listOf(
            fakeSeedEntity(id = 1L, name = "Tomate"),
            fakeSeedEntity(id = 2L, name = "Carotte"),
            fakeSeedEntity(id = 3L, name = "Basilic")
        )
        coEvery { dao.loadSeeds() } returns flowOf(entities)

        val result = repository.getSeedsFlow().first()

        assertEquals(3, result.size)
        assertEquals(listOf("Tomate", "Carotte", "Basilic"), result.map { it.name })
    }

    // ── refreshSeeds ───────────────────────────────────────────────────────────

    @Test
    fun `refreshSeeds calls service and inserts into dao`() = runTest {
        val pagedResponse = fakePagedResponse()
        coEvery { service.listSeeds(any(), any()) } returns pagedResponse
        coJustRun { dao.insertSeeds(any()) }

        repository.refreshSeeds()

        coVerify { service.listSeeds(0, 10) }
        coVerify { dao.insertSeeds(any()) }
    }

    @Test
    fun `refreshSeeds inserts correct entities into dao`() = runTest {
        val seed = fakeSeed(id = 42L, name = "Poivron")
        coEvery { service.listSeeds(any(), any()) } returns fakePagedResponse(listOf(seed))
        coJustRun { dao.insertSeeds(any()) }

        repository.refreshSeeds()

        coVerify {
            dao.insertSeeds(match { entities ->
                entities.size == 1 &&
                        entities[0].id == 42L &&
                        entities[0].name == "Poivron"
            })
        }
    }

    @Test
    fun `refreshSeeds with custom pagination passes correct params`() = runTest {
        coEvery { service.listSeeds(any(), any()) } returns fakePagedResponse()
        coJustRun { dao.insertSeeds(any()) }

        repository.refreshSeeds(pageIndex = 2, countPerPage = 20)

        coVerify { service.listSeeds(2, 20) }
    }

    @Test
    fun `refreshSeeds throws when service fails`() = runTest {
        coEvery { service.listSeeds(any(), any()) } throws RuntimeException("Network error")

        try {
            repository.refreshSeeds()
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals("Network error", e.message)
        }
    }

    // ── createSeed ─────────────────────────────────────────────────────────────

    @Test
    fun `createSeed calls service then refreshes`() = runTest {
        val request = fakeSeedRequest()
        coEvery { service.createSeed(any()) } returns fakeSeed()
        coEvery { service.listSeeds(any(), any()) } returns fakePagedResponse()
        coJustRun { dao.insertSeeds(any()) }

        repository.createSeed(request)

        coVerify { service.createSeed(request) }
        coVerify { service.listSeeds(0, 10) }
    }

    // ── deleteSeed ─────────────────────────────────────────────────────────────

    @Test
    fun `deleteSeed calls service with correct id then refreshes`() = runTest {
        coEvery { service.deleteSeed(any()) } returns mockk()
        coEvery { service.listSeeds(any(), any()) } returns fakePagedResponse()
        coJustRun { dao.insertSeeds(any()) }

        repository.deleteSeed(99L)

        coVerify { service.deleteSeed(99L) }
        coVerify { service.listSeeds(0, 10) }
    }

    // ── updateSeed ─────────────────────────────────────────────────────────────

    @Test
    fun `updateSeed calls service with correct id and seed then refreshes`() = runTest {
        val seed = fakeSeed(id = 5L, name = "Aubergine")
        coEvery { service.updateSeed(any(), any()) } returns seed
        coEvery { service.listSeeds(any(), any()) } returns fakePagedResponse()
        coJustRun { dao.insertSeeds(any()) }

        repository.updateSeed(5L, seed)

        coVerify { service.updateSeed(5L, seed) }
        coVerify { service.listSeeds(0, 10) }
    }
}