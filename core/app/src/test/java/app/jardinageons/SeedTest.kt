package app.jardinageons

import app.jardinageons.data.models.Seed
import org.junit.Assert.*
import org.junit.Test

class SeedTest {

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun fakeSeed(
        id: Long = 1L,
        name: String = "Tomate",
        quantity: Int = 10,
        germinationTime: Int = 7,
        description: String = "Une belle tomate",
        vegetableId: Long? = 42L,
        expiryDate: String = "2026-12-31"
    ) = Seed(id, name, quantity, germinationTime, description, vegetableId, expiryDate)

    // ── Création ───────────────────────────────────────────────────────────────

    @Test
    fun `seed is created with correct values`() {
        val seed = fakeSeed()

        assertEquals(1L, seed.id)
        assertEquals("Tomate", seed.name)
        assertEquals(10, seed.quantity)
        assertEquals(7, seed.germinationTime)
        assertEquals("Une belle tomate", seed.description)
        assertEquals(42L, seed.vegetableId)
        assertEquals("2026-12-31", seed.expiryDate)
    }

    // ── vegetableId nullable ───────────────────────────────────────────────────

    @Test
    fun `vegetableId can be null`() {
        val seed = fakeSeed(vegetableId = null)
        assertNull(seed.vegetableId)
    }

    @Test
    fun `vegetableId can be set`() {
        val seed = fakeSeed(vegetableId = 99L)
        assertEquals(99L, seed.vegetableId)
    }

    // ── Égalité (data class) ───────────────────────────────────────────────────

    @Test
    fun `two seeds with same data are equal`() {
        val seed1 = fakeSeed()
        val seed2 = fakeSeed()
        assertEquals(seed1, seed2)
    }

    @Test
    fun `seeds with different id are not equal`() {
        val seed1 = fakeSeed(id = 1L)
        val seed2 = fakeSeed(id = 2L)
        assertNotEquals(seed1, seed2)
    }

    @Test
    fun `seeds with different name are not equal`() {
        val seed1 = fakeSeed(name = "Tomate")
        val seed2 = fakeSeed(name = "Carotte")
        assertNotEquals(seed1, seed2)
    }

    // ── copy() ────────────────────────────────────────────────────────────────

    @Test
    fun `copy changes only quantity`() {
        val seed = fakeSeed(quantity = 10)
        val updated = seed.copy(quantity = 50)

        assertEquals(50, updated.quantity)
        assertEquals(seed.name, updated.name) // inchangé
        assertEquals(seed.id, updated.id)     // inchangé
    }

    @Test
    fun `copy can update expiryDate`() {
        val seed = fakeSeed(expiryDate = "2026-01-01")
        val updated = seed.copy(expiryDate = "2027-06-15")

        assertEquals("2027-06-15", updated.expiryDate)
    }

    @Test
    fun `copy can set vegetableId to null`() {
        val seed = fakeSeed(vegetableId = 5L)
        val updated = seed.copy(vegetableId = null)

        assertNull(updated.vegetableId)
    }

    // ── Champs métier ──────────────────────────────────────────────────────────

    @Test
    fun `germinationTime zero is valid`() {
        val seed = fakeSeed(germinationTime = 0)
        assertEquals(0, seed.germinationTime)
    }

    @Test
    fun `quantity zero is valid`() {
        val seed = fakeSeed(quantity = 0)
        assertEquals(0, seed.quantity)
    }

    @Test
    fun `description can be empty`() {
        val seed = fakeSeed(description = "")
        assertEquals("", seed.description)
    }

    // ── var expiryDate (mutable) ───────────────────────────────────────────────

    @Test
    fun `expiryDate can be mutated directly`() {
        val seed = fakeSeed(expiryDate = "2026-01-01")
        seed.expiryDate = "2028-05-20"
        assertEquals("2028-05-20", seed.expiryDate)
    }

    // ── hashCode ──────────────────────────────────────────────────────────────

    @Test
    fun `equal seeds have same hashCode`() {
        val seed1 = fakeSeed()
        val seed2 = fakeSeed()
        assertEquals(seed1.hashCode(), seed2.hashCode())
    }

    // ── toString ──────────────────────────────────────────────────────────────

    @Test
    fun `toString contains class name and field values`() {
        val seed = fakeSeed(name = "Basilic")
        val str = seed.toString()
        assertTrue(str.contains("Seed"))
        assertTrue(str.contains("Basilic"))
    }
}