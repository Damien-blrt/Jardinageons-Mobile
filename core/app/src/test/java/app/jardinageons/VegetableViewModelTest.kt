package app.jardinageons

import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.models.Vegetable
import app.jardinageons.data.repositories.VegetableRepository
import app.jardinageons.presentation.features.vegetable.Event
import app.jardinageons.presentation.features.vegetable.VegetableRequest
import app.jardinageons.presentation.features.vegetable.VegetableViewModel
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VegetableViewModelTest {

    private lateinit var repository: VegetableRepository
    private lateinit var viewModel: VegetableViewModel
    private val testDispatcher = StandardTestDispatcher()

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

    private fun fakePagedResponse(items: List<Vegetable> = emptyList()) =
        PagedResponse(totalCount = items.size, pageIndex = 0, countPerPage = 10, items = items)

    // ── Setup / Teardown ───────────────────────────────────────────────────────

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        coEvery { repository.getVegetables(any(), any()) } returns fakePagedResponse()
        viewModel = VegetableViewModel(repository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    // ── État initial ───────────────────────────────────────────────────────────

    @Test
    fun `initial vegetables state is empty`() {
        assertEquals(emptyList<Vegetable>(), viewModel.vegetables.value)
    }

    @Test
    fun `initial isLoading is true`() {
        assertTrue(viewModel.isLoading.value)
    }

    @Test
    fun `isLoading becomes false after load`() = runTest {
        advanceUntilIdle()
        assertFalse(viewModel.isLoading.value)
    }

    // ── loadVegetables ─────────────────────────────────────────────────────────

    @Test
    fun `loadVegetables populates vegetables list`() = runTest {
        val vegetables = listOf(fakeVegetable(name = "Tomate"), fakeVegetable(id = 2L, name = "Carotte"))
        coEvery { repository.getVegetables(any(), any()) } returns fakePagedResponse(vegetables)
        viewModel = VegetableViewModel(repository)
        advanceUntilIdle()

        assertEquals(2, viewModel.vegetables.value.size)
    }

    @Test
    fun `loadVegetables uppercases vegetable names`() = runTest {
        coEvery { repository.getVegetables(any(), any()) } returns fakePagedResponse(listOf(fakeVegetable(name = "tomate")))
        viewModel = VegetableViewModel(repository)
        advanceUntilIdle()

        assertEquals("TOMATE", viewModel.vegetables.value[0].name)
    }

    @Test
    fun `loadVegetables updates totalVegetables`() = runTest {
        val vegetables = listOf(fakeVegetable(), fakeVegetable(id = 2L), fakeVegetable(id = 3L))
        coEvery { repository.getVegetables(any(), any()) } returns fakePagedResponse(vegetables)
        viewModel = VegetableViewModel(repository)
        advanceUntilIdle()

        assertEquals(3, viewModel.totalVegetables.value)
    }

    @Test
    fun `loadVegetables on error sets empty list`() = runTest {
        coEvery { repository.getVegetables(any(), any()) } throws RuntimeException("error")
        viewModel = VegetableViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.vegetables.value.isEmpty())
        assertEquals(0, viewModel.totalVegetables.value)
    }

    @Test
    fun `loadVegetables on error sets isLoading false`() = runTest {
        coEvery { repository.getVegetables(any(), any()) } throws RuntimeException("error")
        viewModel = VegetableViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadVegetables formats ISO date to dd-MM-yyyy`() = runTest {
        val veg = fakeVegetable(sowingStart = "2026-03-15T00:00:00Z")
        coEvery { repository.getVegetables(any(), any()) } returns fakePagedResponse(listOf(veg))
        viewModel = VegetableViewModel(repository)
        advanceUntilIdle()

        assertEquals("15/03/2026", viewModel.vegetables.value[0].sowingStart)
    }

    @Test
    fun `loadVegetables keeps original date if format is unknown`() = runTest {
        val veg = fakeVegetable(sowingStart = "not-a-date")
        coEvery { repository.getVegetables(any(), any()) } returns fakePagedResponse(listOf(veg))
        viewModel = VegetableViewModel(repository)
        advanceUntilIdle()

        assertEquals("not-a-date", viewModel.vegetables.value[0].sowingStart)
    }

    // ── createVegetable ────────────────────────────────────────────────────────

    @Test
    fun `createVegetable emits addSuccess on success`() = runTest {
        coJustRun { repository.createVegetable(any()) }
        val events = mutableListOf<Event>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.createVegetable(fakeRequest())
        advanceUntilIdle()

        assertTrue(events.contains(Event.addSuccess))
        job.cancel()
    }

    @Test
    fun `createVegetable emits addError on failure`() = runTest {
        coEvery { repository.createVegetable(any()) } throws RuntimeException("error")
        val events = mutableListOf<Event>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.createVegetable(fakeRequest())
        advanceUntilIdle()

        assertTrue(events.contains(Event.addError))
        job.cancel()
    }

    @Test
    fun `createVegetable calls repository with correct request`() = runTest {
        val request = fakeRequest()
        coJustRun { repository.createVegetable(any()) }

        viewModel.createVegetable(request)
        advanceUntilIdle()

        coVerify { repository.createVegetable(request) }
    }

    // ── deleteVegetable ────────────────────────────────────────────────────────

    @Test
    fun `deleteVegetable emits deleteSuccess on success`() = runTest {
        coJustRun { repository.deleteVegetable(any()) }
        val events = mutableListOf<Event>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.deleteVegetable(1L)
        advanceUntilIdle()

        assertTrue(events.contains(Event.deleteSuccess))
        job.cancel()
    }

    @Test
    fun `deleteVegetable emits deleteError on failure`() = runTest {
        coEvery { repository.deleteVegetable(any()) } throws RuntimeException("error")
        val events = mutableListOf<Event>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.deleteVegetable(1L)
        advanceUntilIdle()

        assertTrue(events.contains(Event.deleteError))
        job.cancel()
    }

    // ── updateVegetable ────────────────────────────────────────────────────────

    @Test
    fun `updateVegetable emits modifiedSuccess on success`() = runTest {
        coJustRun { repository.updateVegetable(any(), any()) }
        val events = mutableListOf<Event>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.updateVegetable(1L, fakeVegetable())
        advanceUntilIdle()

        assertTrue(events.contains(Event.modifiedSuccess))
        job.cancel()
    }

    @Test
    fun `updateVegetable emits modifiedError on failure`() = runTest {
        coEvery { repository.updateVegetable(any(), any()) } throws RuntimeException("error")
        val events = mutableListOf<Event>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.updateVegetable(1L, fakeVegetable())
        advanceUntilIdle()

        assertTrue(events.contains(Event.modifiedError))
        job.cancel()
    }
}