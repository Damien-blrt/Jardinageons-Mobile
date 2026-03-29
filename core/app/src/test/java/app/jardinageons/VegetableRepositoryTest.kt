package app.jardinageons

import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.models.Vegetable
import app.jardinageons.data.repositories.VegetableRepository
import app.jardinageons.data.services.IVegetableService
import app.jardinageons.presentation.features.vegetable.VegetableRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class VegetableRepositoryTest {

    private lateinit var service: IVegetableService
    private lateinit var repository: VegetableRepository

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun fakeVegetable(
        id: Long = 1L,
        name: String = "Tomate",
        germinationTime: Int = 7,
        description: String = "Description",
        sowingStart: String = "2026-03-01",
        sowingEnd: String = "2026-05-01",
        harvestStart: String = "2026-07-01",
        harvestEnd: String = "2026-09-01",
        waterNeedsMm: Double? = 5.0
    ) = Vegetable(id, name, germinationTime, description, sowingStart, sowingEnd, harvestStart, harvestEnd, waterNeedsMm)

    private fun fakeRequest() = VegetableRequest(
        name = "Courgette",
        germinationTime = 5,
        description = "Une courgette",
        sowingStart = "2026-04-01",
        sowingEnd = "2026-06-01",
        harvestStart = "2026-07-01",
        harvestEnd = "2026-09-01"
    )

    private fun fakePagedResponse(items: List<Vegetable> = listOf(fakeVegetable())) =
        PagedResponse(totalCount = items.size, pageIndex = 0, countPerPage = 10, items = items)

    // ── Setup ──────────────────────────────────────────────────────────────────

    @Before
    fun setup() {
        service = mockk()
        repository = VegetableRepository(service)
    }

    // ── getVegetables ──────────────────────────────────────────────────────────

    @Test
    fun `getVegetables returns paged response from service`() = runTest {
        val expected = fakePagedResponse()
        coEvery { service.listVegetables(any(), any()) } returns expected

        val result = repository.getVegetables(0, 10)

        assertEquals(expected, result)
    }

    @Test
    fun `getVegetables passes correct pagination params`() = runTest {
        coEvery { service.listVegetables(any(), any()) } returns fakePagedResponse()

        repository.getVegetables(pageIndex = 3, countPerPage = 20)

        coVerify { service.listVegetables(3, 20) }
    }

    @Test
    fun `getVegetables returns empty list when service returns empty`() = runTest {
        val empty = PagedResponse<Vegetable>(totalCount = 0, pageIndex = 0, countPerPage = 10, items = emptyList())
        coEvery { service.listVegetables(any(), any()) } returns empty

        val result = repository.getVegetables(0, 10)

        assertTrue(result.items.isEmpty())
        assertEquals(0, result.totalCount)
    }

    @Test
    fun `getVegetables throws when service fails`() = runTest {
        coEvery { service.listVegetables(any(), any()) } throws RuntimeException("Network error")

        try {
            repository.getVegetables(0, 10)
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals("Network error", e.message)
        }
    }

    // ── createVegetable ────────────────────────────────────────────────────────

    @Test
    fun `createVegetable calls service with correct request`() = runTest {
        val request = fakeRequest()
        coEvery { service.createVegetable(any()) } returns fakeVegetable()

        repository.createVegetable(request)

        coVerify { service.createVegetable(request) }
    }

    // ── deleteVegetable ────────────────────────────────────────────────────────

    @Test
    fun `deleteVegetable calls service with correct id`() = runTest {
        coEvery { service.deleteVegetable(any()) } returns mockk()

        repository.deleteVegetable(42L)

        coVerify { service.deleteVegetable(42L) }
    }

    // ── updateVegetable ────────────────────────────────────────────────────────

    @Test
    fun `updateVegetable calls service with correct id and vegetable`() = runTest {
        val vegetable = fakeVegetable(id = 7L, name = "Poivron")
        coEvery { service.updateVegetable(any(), any()) } returns vegetable

        repository.updateVegetable(7L, vegetable)

        coVerify { service.updateVegetable(7L, vegetable) }
    }

    @Test
    fun `updateVegetable uses vegetable id not passed id`() = runTest {
        val vegetable = fakeVegetable(id = 7L)
        coEvery { service.updateVegetable(any(), any()) } returns vegetable

        repository.updateVegetable(99L, vegetable)

        // le repo appelle service.updateVegetable(vegetable.id, ...) donc 7L
        coVerify { service.updateVegetable(7L, vegetable) }
    }

    // ── getVegetableById ──────────────────────────────────────────────────────

    @Test
    fun `getVegetableById returns correct vegetable`() = runTest {
        val expected = fakeVegetable(id = 42L, name = "Aubergine")
        coEvery { service.getVegetableById(42L) } returns expected

        val result = repository.getVegetableById(42L)

        assertEquals(expected, result)
        coVerify { service.getVegetableById(42L) }
    }

    @Test
    fun `getVegetableById throws when service fails`() = runTest {
        coEvery { service.getVegetableById(any()) } throws RuntimeException("Not found")

        try {
            repository.getVegetableById(99L)
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals("Not found", e.message)
        }
    }

    // ── Error path tests ──────────────────────────────────────────────────────

    @Test
    fun `createVegetable throws when service fails`() = runTest {
        coEvery { service.createVegetable(any()) } throws RuntimeException("Create failed")

        try {
            repository.createVegetable(fakeRequest())
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals("Create failed", e.message)
        }
    }

    @Test
    fun `deleteVegetable throws when service fails`() = runTest {
        coEvery { service.deleteVegetable(any()) } throws RuntimeException("Delete failed")

        try {
            repository.deleteVegetable(1L)
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals("Delete failed", e.message)
        }
    }

    @Test
    fun `updateVegetable throws when service fails`() = runTest {
        coEvery { service.updateVegetable(any(), any()) } throws RuntimeException("Update failed")

        try {
            repository.updateVegetable(1L, fakeVegetable())
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals("Update failed", e.message)
        }
    }
}