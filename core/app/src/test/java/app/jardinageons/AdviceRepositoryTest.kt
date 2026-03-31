package app.jardinageons

import app.jardinageons.data.models.Advice
import app.jardinageons.data.models.PagedResponse
import app.jardinageons.data.repositories.AdviceRepository
import app.jardinageons.data.services.IAdviceService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AdviceRepositoryTest {

    private lateinit var api: IAdviceService
    private lateinit var repository: AdviceRepository

    @Before
    fun setup() {
        api = mockk()
        repository = AdviceRepository(api)
    }

    @Test
    fun `getAdvices returns list on successful response`() = runTest {
        val advices = listOf(Advice(1, "Planter en Mars", "Description", "mars"))
        val pagedResponse = PagedResponse(1, 0, 10, advices)
        coEvery { api.getAdvices() } returns Response.success(pagedResponse)

        val result = repository.getAdvices()

        assertEquals(advices, result)
    }

    @Test
    fun `getAdvices returns null on HTTP error`() = runTest {
        coEvery { api.getAdvices() } returns Response.error(404, "Not Found".toResponseBody(null))

        val result = repository.getAdvices()

        assertNull(result)
    }

    @Test
    fun `getAdvices returns null on network exception`() = runTest {
        coEvery { api.getAdvices() } throws RuntimeException("Network timeout")

        val result = repository.getAdvices()

        assertNull(result)
    }
}
