package app.jardinageons

import app.jardinageons.data.models.Harvest
import app.jardinageons.data.repositories.HarvestRepository
import app.jardinageons.presentation.features.harvest.HarvestEvent
import app.jardinageons.presentation.features.harvest.HarvestRequest
import app.jardinageons.presentation.features.harvest.HarvestViewModel
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HarvestViewModelTest {

    private lateinit var repository: HarvestRepository
    private lateinit var viewModel: HarvestViewModel
    private var testDispatcher = UnconfinedTestDispatcher()

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun fakeHarvest(
        id: Long = 1L,
        plantId: Long = 42L,
        date: String = "2026-07-15",
        quantity: Int = 5,
        description: String = "Description"
    ) = Harvest(id, plantId, date, quantity, description)

    // ── Setup / Teardown ───────────────────────────────────────────────────────

    @Before
    fun setup() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        coEvery { repository.getHarvestsFlow() } returns flowOf(emptyList())
        coJustRun { repository.refreshHarvests() }
        viewModel = HarvestViewModel(repository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    // ── État initial ───────────────────────────────────────────────────────────

    @Test
    fun `initial harvests state is empty`() {
        assertEquals(emptyList<Harvest>(), viewModel.harvests.value)
    }

    @Test
    fun `initial totalHarvests is zero`() {
        assertEquals(0, viewModel.totalHarvests.value)
    }

    @Test
    fun `initial isRefreshing is false`() {
        assertFalse(viewModel.isRefreshing.value)
    }

    // ── deleteHarvest ─────────────────────────────────────────────────────────

    @Test
    fun `deleteHarvest emits deleteSuccess on success`() = runTest {
        coJustRun { repository.deleteHarvest(any()) }
        val events = mutableListOf<HarvestEvent>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.deleteHarvest(1L)
        advanceUntilIdle()

        assertTrue(events.contains(HarvestEvent.deleteSuccess))
        job.cancel()
    }

    @Test
    fun `deleteHarvest emits deleteError on failure`() = runTest {
        coEvery { repository.deleteHarvest(any()) } throws RuntimeException("error")
        val events = mutableListOf<HarvestEvent>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.deleteHarvest(1L)
        advanceUntilIdle()

        assertTrue(events.contains(HarvestEvent.deleteError))
        job.cancel()
    }

    @Test
    fun `deleteHarvest calls repository with correct id`() = runTest {
        coJustRun { repository.deleteHarvest(any()) }

        viewModel.deleteHarvest(99L)
        advanceUntilIdle()

        coVerify { repository.deleteHarvest(99L) }
    }

    // ── updateHarvest ─────────────────────────────────────────────────────────

    @Test
    fun `updateHarvest emits modifiedSuccess on success`() = runTest {
        coJustRun { repository.updateHarvest(any(), any()) }
        val events = mutableListOf<HarvestEvent>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.updateHarvest(1L, fakeHarvest())
        advanceUntilIdle()

        assertTrue(events.contains(HarvestEvent.modifiedSuccess))
        job.cancel()
    }

    @Test
    fun `updateHarvest emits modifiedError on failure`() = runTest {
        coEvery { repository.updateHarvest(any(), any()) } throws RuntimeException("error")
        val events = mutableListOf<HarvestEvent>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.updateHarvest(1L, fakeHarvest())
        advanceUntilIdle()

        assertTrue(events.contains(HarvestEvent.modifiedError))
        job.cancel()
    }

    // ── HarvestRequest ─────────────────────────────────────────────────────────

    @Test
    fun `HarvestRequest fields are correctly set`() {
        val request = HarvestRequest(
            plantId = 10L,
            date = "2026-08-01",
            quantity = 15,
            description = "Récolte abondante"
        )
        assertEquals(10L, request.plantId)
        assertEquals(15, request.quantity)
    }
}
