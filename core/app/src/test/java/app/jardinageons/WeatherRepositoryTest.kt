package app.jardinageons

import app.jardinageons.data.models.WeatherSummary
import app.jardinageons.data.repositories.WeatherRepository
import org.junit.Assert.*
import org.junit.Test

class WeatherRepositoryTest {

    private val repository = WeatherRepository()

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun fakeWeatherSummary(
        rainTotal24h: Double = 2.5,
        currentTemp: Double? = 18.0,
        locationName: String = "Clermont-Ferrand",
        humidity: Int? = 65,
        windSpeedKmh: Double? = 12.0
    ) = WeatherSummary(rainTotal24h, currentTemp, locationName, humidity, windSpeedKmh)

    // ── WeatherSummary ──────────────────────────────────────────────────────────

    @Test
    fun `WeatherSummary has correct default values`() {
        val summary = fakeWeatherSummary()

        assertEquals(2.5, summary.rainTotal24h, 0.001)
        assertEquals(18.0, summary.currentTemp)
        assertEquals("Clermont-Ferrand", summary.locationName)
        assertEquals(65, summary.humidity)
        assertEquals(12.0, summary.windSpeedKmh)
    }

    @Test
    fun `WeatherSummary currentTemp can be null`() {
        val summary = fakeWeatherSummary(currentTemp = null)
        assertNull(summary.currentTemp)
    }

    @Test
    fun `WeatherSummary humidity can be null`() {
        val summary = fakeWeatherSummary(humidity = null)
        assertNull(summary.humidity)
    }

    @Test
    fun `WeatherSummary windSpeedKmh can be null`() {
        val summary = fakeWeatherSummary(windSpeedKmh = null)
        assertNull(summary.windSpeedKmh)
    }

    @Test
    fun `WeatherSummary rainTotal24h zero is valid`() {
        val summary = fakeWeatherSummary(rainTotal24h = 0.0)
        assertEquals(0.0, summary.rainTotal24h, 0.001)
    }
}