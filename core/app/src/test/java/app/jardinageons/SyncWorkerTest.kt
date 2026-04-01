package app.jardinageons

import android.content.Context
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import app.jardinageons.data.workers.SyncWorker
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SyncWorkerTest {

    private lateinit var mockContext: Context
    private lateinit var mockParams: WorkerParameters

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockParams = mockk(relaxed = true)

        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
        every { android.util.Log.w(any(), any<String>()) } returns 0
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `SyncWorker returns failure when no refresh token`() = runTest {
        // Sans refresh token, le worker doit retourner failure immédiatement
        app.jardinageons.data.storage.TokenManager.refreshToken = null

        val worker = SyncWorker(mockContext, mockParams)
        val result = worker.doWork()

        assertTrue(result is Result.Failure)
    }
}
