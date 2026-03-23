package app.jardinageons

import app.jardinageons.data.models.Seed
import app.jardinageons.data.repositories.SeedRepository
import app.jardinageons.presentation.features.seedInventory.Event
import app.jardinageons.presentation.features.seedInventory.SeedInventoryViewModel
import app.jardinageons.presentation.features.seedInventory.SeedRequest
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class SeedInventoryViewModelTest {

    private lateinit var repository: SeedRepository
    private lateinit var viewModel: SeedInventoryViewModel
    private val testDispatcher = StandardTestDispatcher()

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

    private fun fakeSeedRequest() = SeedRequest(
        name = "Carotte",
        quantity = 5,
        germinationTime = 10,
        description = "Une carotte",
        vegetableId = 0,
        expiryDate = "2027-01-01"
    )

    // ── Setup / Teardown ───────────────────────────────────────────────────────

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        coEvery { repository.getSeedsFlow() } returns flowOf(emptyList())
        coJustRun { repository.refreshSeeds(any(), any()) }
        viewModel = SeedInventoryViewModel(repository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    // ── État initial ───────────────────────────────────────────────────────────

    @Test
    fun `initial seeds state is empty`() = runTest {
        assertEquals(emptyList<Seed>(), viewModel.seeds.value)
    }

    @Test
    fun `initial isLoading is true`() {
        assertTrue(viewModel.isLoading.value)
    }

    @Test
    fun `isLoading becomes false after local seeds loaded`() = runTest {
        coEvery { repository.getSeedsFlow() } returns flowOf(listOf(fakeSeed()))
        viewModel = SeedInventoryViewModel(repository)
        advanceUntilIdle()
        assertFalse(viewModel.isLoading.value)
    }

    // ── getSeedsFlow / observeLocalSeeds ───────────────────────────────────────

    @Test
    fun `seeds updated when repository emits new list`() = runTest {
        val seeds = listOf(fakeSeed(name = "Tomate"), fakeSeed(id = 2L, name = "Carotte"))
        coEvery { repository.getSeedsFlow() } returns flowOf(seeds)
        viewModel = SeedInventoryViewModel(repository)
        advanceUntilIdle()

        assertEquals(2, viewModel.seeds.value.size)
    }

    @Test
    fun `seed names are uppercased after normalization`() = runTest {
        coEvery { repository.getSeedsFlow() } returns flowOf(listOf(fakeSeed(name = "tomate")))
        viewModel = SeedInventoryViewModel(repository)
        advanceUntilIdle()

        assertEquals("TOMATE", viewModel.seeds.value[0].name)
    }

    // ── totalSeeds ─────────────────────────────────────────────────────────────

    @Test
    fun `totalSeeds sums quantities correctly`() = runTest {
        val seeds = listOf(fakeSeed(quantity = 5), fakeSeed(id = 2L, quantity = 15))
        coEvery { repository.getSeedsFlow() } returns flowOf(seeds)
        viewModel = SeedInventoryViewModel(repository)
        advanceUntilIdle()

        assertEquals(20, viewModel.totalSeeds.value)
    }

    @Test
    fun `totalSeeds is zero when list is empty`() = runTest {
        coEvery { repository.getSeedsFlow() } returns flowOf(emptyList())
        viewModel = SeedInventoryViewModel(repository)
        advanceUntilIdle()

        assertEquals(0, viewModel.totalSeeds.value)
    }

    // ── averageGerminationTime ─────────────────────────────────────────────────

    @Test
    fun `averageGerminationTime calculates correctly`() = runTest {
        val seeds = listOf(
            fakeSeed(germinationTime = 4),
            fakeSeed(id = 2L, germinationTime = 10)
        )
        coEvery { repository.getSeedsFlow() } returns flowOf(seeds)
        viewModel = SeedInventoryViewModel(repository)
        advanceUntilIdle()

        assertEquals(7, viewModel.averageGerminationTime.value)
    }

    @Test
    fun `averageGerminationTime is zero when list is empty`() = runTest {
        coEvery { repository.getSeedsFlow() } returns flowOf(emptyList())
        viewModel = SeedInventoryViewModel(repository)
        advanceUntilIdle()

        assertEquals(0, viewModel.averageGerminationTime.value)
    }

    // ── createSeed ─────────────────────────────────────────────────────────────

    @Test
    fun `createSeed emits addSuccess on success`() = runTest {
        coJustRun { repository.createSeed(any()) }
        val events = mutableListOf<Event>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.createSeed(fakeSeedRequest())
        advanceUntilIdle()

        assertTrue(events.contains(Event.addSuccess))
        job.cancel()
    }

    @Test
    fun `createSeed emits addError on failure`() = runTest {
        coEvery { repository.createSeed(any()) } throws RuntimeException("error")
        val events = mutableListOf<Event>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.createSeed(fakeSeedRequest())
        advanceUntilIdle()

        assertTrue(events.contains(Event.addError))
        job.cancel()
    }

    @Test
    fun `createSeed calls repository with correct request`() = runTest {
        val request = fakeSeedRequest()
        coJustRun { repository.createSeed(any()) }

        viewModel.createSeed(request)
        advanceUntilIdle()

        coVerify { repository.createSeed(request) }
    }

    // ── deleteSeed ─────────────────────────────────────────────────────────────

    @Test
    fun `deleteSeed emits deleteSuccess on success`() = runTest {
        coJustRun { repository.deleteSeed(any()) }
        val events = mutableListOf<Event>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.deleteSeed(1L)
        advanceUntilIdle()

        assertTrue(events.contains(Event.deleteSuccess))
        job.cancel()
    }

    @Test
    fun `deleteSeed emits deleteError on failure`() = runTest {
        coEvery { repository.deleteSeed(any()) } throws RuntimeException("error")
        val events = mutableListOf<Event>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.deleteSeed(1L)
        advanceUntilIdle()

        assertTrue(events.contains(Event.deleteError))
        job.cancel()
    }

    // ── updateSeed ─────────────────────────────────────────────────────────────

    @Test
    fun `updateSeed emits modifiedSuccess on success`() = runTest {
        coJustRun { repository.updateSeed(any(), any()) }
        val events = mutableListOf<Event>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.updateSeed(1L, fakeSeed())
        advanceUntilIdle()

        assertTrue(events.contains(Event.modifiedSuccess))
        job.cancel()
    }

    @Test
    fun `updateSeed emits modifiedError on failure`() = runTest {
        coEvery { repository.updateSeed(any(), any()) } throws RuntimeException("error")
        val events = mutableListOf<Event>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.updateSeed(1L, fakeSeed())
        advanceUntilIdle()

        assertTrue(events.contains(Event.modifiedError))
        job.cancel()
    }
}