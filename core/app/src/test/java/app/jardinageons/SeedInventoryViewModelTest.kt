package app.jardinageons

import app.jardinageons.data.models.Seed
import app.jardinageons.data.repositories.SeedRepository
import app.jardinageons.presentation.features.seedInventory.SeedInventoryViewModel
import app.jardinageons.presentation.features.seedInventory.SeedRequest
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

// Ces tests ne font qu'inspecter l'état synchrone initial du ViewModel.
// Pas de setMain, pas de coroutines, pas de dépendance au main looper Android.
class SeedInventoryViewModelTest {

    private lateinit var repository: SeedRepository
    private lateinit var viewModel: SeedInventoryViewModel

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

    // ── Setup ──────────────────────────────────────────────────────────────────

    @Before
    fun setup() {
        repository = mockk()
        coEvery { repository.getSeedsFlow() } returns flowOf(emptyList())
        coJustRun { repository.refreshSeeds() }
        viewModel = SeedInventoryViewModel(repository)
    }

    // ── État initial (synchrone, toujours testable) ────────────────────────────

    @Test
    fun `initial seeds state is empty`() {
        assertEquals(emptyList<Seed>(), viewModel.seeds.value)
    }

    @Test
    fun `initial isLoading is true`() {
        assertTrue(viewModel.isLoading.value)
    }

    @Test
    fun `initial totalSeeds is zero`() {
        assertEquals(0, viewModel.totalSeeds.value)
    }

    @Test
    fun `initial averageGerminationTime is zero`() {
        assertEquals(0, viewModel.averageGerminationTime.value)
    }

    @Test
    fun `initial isRefreshing is false`() {
        assertFalse(viewModel.isRefreshing.value)
    }

    // ── Vérification des StateFlow (synchrone) ─────────────────────────────────

    @Test
    fun `seeds StateFlow is not null`() {
        assertNotNull(viewModel.seeds)
    }

    @Test
    fun `uiEvent SharedFlow is not null`() {
        assertNotNull(viewModel.uiEvent)
    }

    @Test
    fun `SeedRequest fields are correctly set`() {
        val request = fakeSeedRequest()
        assertEquals("Carotte", request.name)
        assertEquals(5, request.quantity)
        assertEquals(10, request.germinationTime)
        assertEquals("Une carotte", request.description)
        assertEquals("2027-01-01", request.expiryDate)
    }

    @Test
    fun `fakeSeed default fields are correctly set`() {
        val seed = fakeSeed()
        assertEquals(1L, seed.id)
        assertEquals("Tomate", seed.name)
        assertEquals(10, seed.quantity)
        assertEquals(7, seed.germinationTime)
    }
}