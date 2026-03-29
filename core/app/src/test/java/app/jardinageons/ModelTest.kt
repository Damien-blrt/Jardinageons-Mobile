package app.jardinageons

import app.jardinageons.data.entities.HarvestEntity
import app.jardinageons.data.entities.SeedEntity
import app.jardinageons.data.models.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests des modèles de données du projet.
 * On ne teste que les comportements métier significatifs,
 * pas les fonctionnalités auto-générées par Kotlin (equals, hashCode, toString, copy).
 */
class ModelTest {

    // ── Seed ───────────────────────────────────────────────────────────────────

    @Test
    fun `seed is created with correct values`() {
        val seed = Seed(1L, "Tomate", 10, 7, "Description", 42L, "2026-12-31")
        assertEquals(1L, seed.id)
        assertEquals("Tomate", seed.name)
        assertEquals(10, seed.quantity)
        assertEquals(7, seed.germinationTime)
        assertEquals(42L, seed.vegetableId)
    }

    @Test
    fun `seed vegetableId can be null`() {
        val seed = Seed(1L, "Tomate", 10, 7, "desc", null, "2026-12-31")
        assertNull(seed.vegetableId)
    }

    @Test
    fun `seed expiryDate is mutable`() {
        val seed = Seed(1L, "Tomate", 10, 7, "desc", null, "2026-01-01")
        seed.expiryDate = "2028-05-20"
        assertEquals("2028-05-20", seed.expiryDate)
    }

    @Test
    fun `seed quantity zero is valid`() {
        val seed = Seed(1L, "Tomate", 0, 7, "desc", null, "2026-12-31")
        assertEquals(0, seed.quantity)
    }

    // ── Harvest ────────────────────────────────────────────────────────────────

    @Test
    fun `harvest is created with correct values`() {
        val harvest = Harvest(1L, 42L, "2026-07-15", 5, "Tomates")
        assertEquals(1L, harvest.id)
        assertEquals(42L, harvest.plantId)
        assertEquals("2026-07-15", harvest.date)
        assertEquals(5, harvest.quantity)
        assertEquals("Tomates", harvest.description)
    }

    @Test
    fun `harvest quantity zero is valid`() {
        val harvest = Harvest(1L, 42L, "2026-07-15", 0, "")
        assertEquals(0, harvest.quantity)
    }

    // ── Vegetable ──────────────────────────────────────────────────────────────

    @Test
    fun `vegetable is created with correct values`() {
        val veg = Vegetable(1L, "Tomate", 7, "desc", "s1", "s2", "h1", "h2", 5.0)
        assertEquals(1L, veg.id)
        assertEquals("Tomate", veg.name)
        assertEquals(5.0, veg.waterNeedsMm)
    }

    @Test
    fun `vegetable waterNeedsMm defaults to 5`() {
        val veg = Vegetable(1L, "Tomate", 7, "desc", "s1", "s2", "h1", "h2")
        assertEquals(5.0, veg.waterNeedsMm)
    }

    @Test
    fun `vegetable waterNeedsMm can be null`() {
        val veg = Vegetable(1L, "Tomate", 7, "desc", "s1", "s2", "h1", "h2", null)
        assertNull(veg.waterNeedsMm)
    }

    // ── Advice ─────────────────────────────────────────────────────────────────

    @Test
    fun `advice is created with correct values`() {
        val advice = Advice(1, "Arrosage", "Arrosez le matin", "mars")
        assertEquals(1, advice.id)
        assertEquals("Arrosage", advice.titre)
        assertEquals("mars", advice.month)
    }

    @Test
    fun `advice month defaults to null`() {
        val advice = Advice(1, "Arrosage", "conseil")
        assertNull(advice.month)
    }

    // ── WeatherSummary ─────────────────────────────────────────────────────────

    @Test
    fun `weatherSummary is created with correct values`() {
        val summary = WeatherSummary(2.5, 18.0, "Clermont-Ferrand", 65, 12.0)
        assertEquals(2.5, summary.rainTotal24h, 0.001)
        assertEquals(18.0, summary.currentTemp)
        assertEquals("Clermont-Ferrand", summary.locationName)
    }

    @Test
    fun `weatherSummary nullable fields can be null`() {
        val summary = WeatherSummary(0.0, null, "Paris", null, null)
        assertNull(summary.currentTemp)
        assertNull(summary.humidity)
        assertNull(summary.windSpeedKmh)
    }

    // ── Weather models ─────────────────────────────────────────────────────────

    @Test
    fun `forecast rain can be null`() {
        val forecast = ForeCast("2026-03-27", MainWeather(20.0, 65), null, null)
        assertNull(forecast.rain)
        assertNull(forecast.wind)
    }

    @Test
    fun `rain threeHour can be null`() {
        val rain = Rain(null)
        assertNull(rain.threeHour)
    }

    @Test
    fun `weatherResponse city can be null`() {
        val response = WeatherResponse(emptyList(), null)
        assertNull(response.city)
    }

    // ── Entités Room ───────────────────────────────────────────────────────────

    @Test
    fun `seedEntity maps all fields`() {
        val entity = SeedEntity(1L, "Tomate", 10, 7, "desc", 42L, "2026-12-31")
        assertEquals(1L, entity.id)
        assertEquals("Tomate", entity.name)
        assertEquals(42L, entity.vegetableId)
    }

    @Test
    fun `harvestEntity maps all fields`() {
        val entity = HarvestEntity(1L, 42L, "2026-07-15", 5, "desc")
        assertEquals(1L, entity.id)
        assertEquals(42L, entity.plantId)
        assertEquals(5, entity.quantity)
    }

    // ── PagedResponse ──────────────────────────────────────────────────────────

    @Test
    fun `pagedResponse is created correctly`() {
        val response = PagedResponse(50, 0, 10, listOf("A", "B"))
        assertEquals(50, response.totalCount)
        assertEquals(2, response.items.size)
    }

    @Test
    fun `pagedResponse with empty items`() {
        val response = PagedResponse<String>(0, 0, 10, emptyList())
        assertTrue(response.items.isEmpty())
    }

    // ── Logique métier Weather ──────────────────────────────────────────────────

    @Test
    fun `wind speed ms to kmh conversion`() {
        val windMs = 10.0
        assertEquals(36.0, windMs * 3.6, 0.001)
    }

    @Test
    fun `rain accumulation sums over forecasts`() {
        val forecasts = listOf(1.0, 0.5, 0.0, 2.0, 0.3, 0.0, 1.2, 0.8)
        assertEquals(5.8, forecasts.take(8).sum(), 0.001)
    }

    @Test
    fun `rain accumulation ignores null values`() {
        val forecasts = listOf<Double?>(1.0, null, 0.5, null, 2.0)
        assertEquals(3.5, forecasts.sumOf { it ?: 0.0 }, 0.001)
    }

    @Test
    fun `city name fallback when null`() {
        val cityName: String? = null
        assertEquals("Mon Jardin", cityName ?: "Mon Jardin")
    }

    // ── Garden ─────────────────────────────────────────────────────────────────

    @Test
    fun `garden is created correctly`() {
        val garden = Garden()
        assertNotNull(garden)
    }

    // ── Grow ───────────────────────────────────────────────────────────────────

    @Test
    fun `grow is created with correct values`() {
        val grow = Grow(1L, 42L, "2026-05-10", 3)
        assertEquals(1L, grow.id)
        assertEquals(42L, grow.vegetableId)
        assertEquals("2026-05-10", grow.plantingDate)
        assertEquals(3, grow.quantity)
    }

    @Test
    fun `grow defaults are applied correctly`() {
        val grow = Grow(1L, 42L)
        assertNull(grow.plantingDate)
        assertEquals(1, grow.quantity)
    }

    // ── Auth Models ────────────────────────────────────────────────────────────

    @Test
    fun `loginRequest is created correctly`() {
        val request = LoginRequest("test@test.com", "password123")
        assertEquals("test@test.com", request.email)
        assertEquals("password123", request.password)
    }

    @Test
    fun `loginResponse is mapped correctly`() {
        val response = LoginResponse("Bearer", "abc.def.ghi", 3600L, "refresh_token_123")
        assertEquals("Bearer", response.tokenType)
        assertEquals("abc.def.ghi", response.accessToken)
        assertEquals(3600L, response.expiresIn)
        assertEquals("refresh_token_123", response.refreshToken)
    }

    @Test
    fun `refreshRequest is created correctly`() {
        val request = RefreshRequest("my_refresh_token")
        assertEquals("my_refresh_token", request.refreshToken)
    }
}
