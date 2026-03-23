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

        assertThrows(RuntimeException::class.java) {
            runTest { repository.getVegetables(0, 10) }
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
}