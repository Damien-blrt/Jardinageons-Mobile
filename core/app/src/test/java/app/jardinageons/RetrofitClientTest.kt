package app.jardinageons

import app.jardinageons.data.services.RetrofitClient
import org.junit.Assert.assertNotNull
import org.junit.Test

class RetrofitClientTest {

    @Test
    fun `seedService is successfully created`() {
        val service = RetrofitClient.seedService
        assertNotNull(service)
    }

    @Test
    fun `vegetableService is successfully created`() {
        val service = RetrofitClient.vegetableService
        assertNotNull(service)
    }

    @Test
    fun `adviceService is successfully created`() {
        val service = RetrofitClient.adviceService
        assertNotNull(service)
    }

    @Test
    fun `loginQService is successfully created`() {
        val service = RetrofitClient.loginQService
        assertNotNull(service)
    }

    @Test
    fun `harvestService is successfully created`() {
        val service = RetrofitClient.harvestService
        assertNotNull(service)
    }

    @Test
    fun `weatherService is successfully created`() {
        val service = RetrofitClient.weatherService
        assertNotNull(service)
    }
}
