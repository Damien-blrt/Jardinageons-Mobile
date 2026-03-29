package app.jardinageons

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.jardinageons.data.dao.SeedDao
import app.jardinageons.data.database.JardinageonsDatabase
import app.jardinageons.data.entities.SeedEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SeedDaoTest {

    private lateinit var db: JardinageonsDatabase
    private lateinit var dao: SeedDao

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun fakeSeed(
        id: Long = 0L,
        name: String = "Tomate",
        quantity: Int = 10,
        germinationTime: Int = 7,
        description: String = "Une belle tomate",
        vegetableId: Long? = null,
        expiryDate: String = "2026-12-31"
    ) = SeedEntity(id, name, quantity, germinationTime, description, vegetableId, expiryDate)

    // ── Setup / Teardown ───────────────────────────────────────────────────────

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, JardinageonsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.seedDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    // ── insertSeed ─────────────────────────────────────────────────────────────

    @Test
    fun insertSeed_andLoadSeeds_returnsInsertedSeed() = runTest {
        val seed = fakeSeed(name = "Carotte")
        dao.insertSeed(seed)

        val result = dao.loadSeeds().first()
        assertEquals(1, result.size)
        assertEquals("Carotte", result[0].name)
    }

    @Test
    fun insertSeed_withConflict_replacesExisting() = runTest {
        dao.insertSeed(fakeSeed(id = 1L, name = "Tomate", quantity = 5))
        dao.insertSeed(fakeSeed(id = 1L, name = "Tomate", quantity = 99))

        val result = dao.loadSeeds().first()
        assertEquals(1, result.size)
        assertEquals(99, result[0].quantity)
    }

    // ── insertSeeds ────────────────────────────────────────────────────────────

    @Test
    fun insertSeeds_insertsAllSeeds() = runTest {
        val seeds = listOf(
            fakeSeed(name = "Tomate"),
            fakeSeed(name = "Carotte"),
            fakeSeed(name = "Basilic")
        )
        dao.insertSeeds(seeds)

        val result = dao.loadSeeds().first()
        assertEquals(3, result.size)
    }

    @Test
    fun insertSeeds_emptyList_doesNothing() = runTest {
        dao.insertSeeds(emptyList())

        val result = dao.loadSeeds().first()
        assertTrue(result.isEmpty())
    }

    // ── updateSeed ─────────────────────────────────────────────────────────────

    @Test
    fun updateSeed_changesQuantity() = runTest {
        dao.insertSeed(fakeSeed(id = 1L, quantity = 10))
        dao.updateSeed(fakeSeed(id = 1L, quantity = 50))

        val result = dao.loadSeeds().first()
        assertEquals(50, result[0].quantity)
    }

    @Test
    fun updateSeed_changesExpiryDate() = runTest {
        dao.insertSeed(fakeSeed(id = 1L, expiryDate = "2026-01-01"))
        dao.updateSeed(fakeSeed(id = 1L, expiryDate = "2028-06-15"))

        val result = dao.loadSeeds().first()
        assertEquals("2028-06-15", result[0].expiryDate)
    }

    // ── deleteSeed ─────────────────────────────────────────────────────────────

    @Test
    fun deleteSeed_removesCorrectSeed() = runTest {
        val seed1 = fakeSeed(id = 1L, name = "Tomate")
        val seed2 = fakeSeed(id = 2L, name = "Carotte")
        dao.insertSeeds(listOf(seed1, seed2))

        dao.deleteSeed(seed1)

        val result = dao.loadSeeds().first()
        assertEquals(1, result.size)
        assertEquals("Carotte", result[0].name)
    }

    @Test
    fun deleteSeed_onEmptyDb_doesNotCrash() = runTest {
        dao.deleteSeed(fakeSeed(id = 99L))

        val result = dao.loadSeeds().first()
        assertTrue(result.isEmpty())
    }

    // ── clearSeeds ─────────────────────────────────────────────────────────────

    @Test
    fun clearSeeds_removesAllSeeds() = runTest {
        dao.insertSeeds(listOf(fakeSeed(name = "Tomate"), fakeSeed(name = "Carotte")))
        dao.clearSeeds()

        val result = dao.loadSeeds().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun clearSeeds_onEmptyDb_doesNotCrash() = runTest {
        dao.clearSeeds()

        val result = dao.loadSeeds().first()
        assertTrue(result.isEmpty())
    }

    // ── loadSeeds (Flow) ───────────────────────────────────────────────────────

    @Test
    fun loadSeeds_emptyDb_returnsEmptyList() = runTest {
        val result = dao.loadSeeds().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun loadSeeds_withVegetableId_preservesValue() = runTest {
        dao.insertSeed(fakeSeed(id = 1L, vegetableId = 42L))

        val result = dao.loadSeeds().first()
        assertEquals(42L, result[0].vegetableId)
    }

    @Test
    fun loadSeeds_withNullVegetableId_preservesNull() = runTest {
        dao.insertSeed(fakeSeed(id = 1L, vegetableId = null))

        val result = dao.loadSeeds().first()
        assertNull(result[0].vegetableId)
    }
}