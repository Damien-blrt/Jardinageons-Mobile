package app.jardinageons

import app.jardinageons.data.dao.HarvestDao
import app.jardinageons.data.entities.HarvestEntity
import app.jardinageons.data.models.Harvest
import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.repositories.HarvestRepository
import app.jardinageons.data.services.HarvestService
import app.jardinageons.presentation.features.harvest.HarvestRequest
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

class HarvestRepositoryTest {

    private lateinit var service: HarvestService
    private lateinit var dao: HarvestDao
    private lateinit var repository: HarvestRepository

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun fakeHarvest(
        id: Long = 1L,
        plantId: Long = 42L,
        quantity: Int = 5,
        description: String = "Description",
        date: String = "2026-07-15"
    ) = Harvest(id, plantId, date, quantity, description)

    private fun fakeHarvestEntity(
        id: Long = 1L,
        plantId: Long = 42L,
        quantity: Int = 5,
        description: String = "Description",
        date: String = "2026-07-15"
    ) = HarvestEntity(id, plantId, date, quantity, description)

    private fun fakePagedResponse(harvests: List<Harvest> = listOf(fakeHarvest())) =
        PagedResponse(totalCount = harvests.size, pageIndex = 0, countPerPage = 10, items = harvests)

    private fun fakeHarvestRequest() = HarvestRequest(
        plantId = 42L,
        date = "2026-08-01",
        quantity = 10,
        description = "Nouvelle récolte"
    )

    // ── Setup ──────────────────────────────────────────────────────────────────

    @Before
    fun setup() {
        service = mockk()
        dao = mockk()
        repository = HarvestRepository(service, dao)
    }

    // ── getHarvestsFlow ─────────────────────────────────────────────────────────

    @Test
    fun `getHarvestsFlow maps entities to Harvest models`() = runTest {
        val entity = fakeHarvestEntity(id = 1L, plantId = 42L)
        coEvery { dao.loadHarvests() } returns flowOf(listOf(entity))

        val result = repository.getHarvestsFlow().first()

        assertEquals(1, result.size)
        assertEquals(42L, result[0].plantId)
        assertEquals(1L, result[0].id)
    }

    @Test
    fun `getHarvestsFlow returns empty list when dao is empty`() = runTest {
        coEvery { dao.loadHarvests() } returns flowOf(emptyList())

        val result = repository.getHarvestsFlow().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getHarvestsFlow maps multiple entities correctly`() = runTest {
        val entities = listOf(
            fakeHarvestEntity(id = 1L, description = "Tomates"),
            fakeHarvestEntity(id = 2L, description = "Carottes"),
            fakeHarvestEntity(id = 3L, description = "Poivrons")
        )
        coEvery { dao.loadHarvests() } returns flowOf(entities)

        val result = repository.getHarvestsFlow().first()

        assertEquals(3, result.size)
        assertEquals(listOf("Tomates", "Carottes", "Poivrons"), result.map { it.description })
    }

    // ── refreshHarvests ─────────────────────────────────────────────────────────

    @Test
    fun `refreshHarvests calls service and inserts into dao`() = runTest {
        val pagedResponse = fakePagedResponse()
        coEvery { service.listHarvests(any(), any()) } returns pagedResponse
        coJustRun { dao.insertHarvests(any()) }

        repository.refreshHarvests()

        coVerify { service.listHarvests(0, 10) }
        coVerify { dao.insertHarvests(any()) }
    }

    @Test
    fun `refreshHarvests inserts correct entities into dao`() = runTest {
        val harvest = fakeHarvest(id = 42L, description = "Poivrons")
        coEvery { service.listHarvests(any(), any()) } returns fakePagedResponse(listOf(harvest))
        coJustRun { dao.insertHarvests(any()) }

        repository.refreshHarvests()

        coVerify {
            dao.insertHarvests(match { entities ->
                entities.size == 1 &&
                        entities[0].id == 42L &&
                        entities[0].description == "Poivrons"
            })
        }
    }

    @Test
    fun `refreshHarvests with custom pagination passes correct params`() = runTest {
        coEvery { service.listHarvests(any(), any()) } returns fakePagedResponse()
        coJustRun { dao.insertHarvests(any()) }

        repository.refreshHarvests(pageIndex = 2, countPerPage = 20)

        coVerify { service.listHarvests(2, 20) }
    }

    @Test
    fun `refreshHarvests throws when service fails`() = runTest {
        coEvery { service.listHarvests(any(), any()) } throws RuntimeException("Network error")

        try {
            repository.refreshHarvests()
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals("Network error", e.message)
        }
    }

    // ── createHarvest ─────────────────────────────────────────────────────────

    @Test
    fun `createHarvest calls service then refreshes`() = runTest {
        val request = fakeHarvestRequest()
        coEvery { service.createHarvest(any()) } returns fakeHarvest()
        coEvery { service.listHarvests(any(), any()) } returns fakePagedResponse()
        coJustRun { dao.insertHarvests(any()) }

        repository.createHarvest(request)

        coVerify { service.createHarvest(request) }
        coVerify { service.listHarvests(0, 10) }
    }

    // ── deleteHarvest ─────────────────────────────────────────────────────────

    @Test
    fun `deleteHarvest calls service with correct id then refreshes`() = runTest {
        coEvery { service.deleteHarvest(any()) } returns mockk()
        coEvery { service.listHarvests(any(), any()) } returns fakePagedResponse()
        coJustRun { dao.insertHarvests(any()) }

        repository.deleteHarvest(99L)

        coVerify { service.deleteHarvest(99L) }
        coVerify { service.listHarvests(0, 10) }
    }

    // ── updateHarvest ─────────────────────────────────────────────────────────

    @Test
    fun `updateHarvest calls service with correct id and harvest then refreshes`() = runTest {
        val harvest = fakeHarvest(id = 5L, description = "Aubergines")
        coEvery { service.updateHarvest(any(), any()) } returns harvest
        coEvery { service.listHarvests(any(), any()) } returns fakePagedResponse()
        coJustRun { dao.insertHarvests(any()) }

        repository.updateHarvest(5L, harvest)

        coVerify { service.updateHarvest(5L, harvest) }
        coVerify { service.listHarvests(0, 10) }
    }
}
