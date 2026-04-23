package app.jardinageons

import app.jardinageons.data.models.Grow
import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.repositories.GrowsRepository
import app.jardinageons.data.services.IGrowService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class GrowsRepositoryTest {

    private lateinit var service: IGrowService
    private lateinit var repository: GrowsRepository

    @Before
    fun setup() {
        service = mockk()
        repository = GrowsRepository(service)
    }

    @Test
    fun `getGrows returns successful paged response`() = runTest {
        val expected = PagedResponse(1, 0, 10, listOf(Grow(1L, 42L, "2026-05-15", 3)))
        coEvery { service.listGrows(any(), any()) } returns expected

        val result = repository.getGrows(0, 10)

        assertEquals(expected, result)
        coVerify { service.listGrows(0, 10) }
    }

    @Test
    fun `getGrows propagates network exception`() = runTest {
        coEvery { service.listGrows(any(), any()) } throws RuntimeException("Network Error")

        try {
            repository.getGrows(0, 10)
            fail("Expected RuntimeException")
        } catch (e: Exception) {
            assertEquals("Network Error", e.message)
        }
    }
}
