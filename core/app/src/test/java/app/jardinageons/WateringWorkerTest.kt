package app.jardinageons

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import app.jardinageons.data.workers.WateringWorker
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor

class WateringWorkerTest {

    private lateinit var mockContext: Context
    private lateinit var mockParams: WorkerParameters
    private lateinit var mockNotificationManager: NotificationManager

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockParams = mockk(relaxed = true)
        mockNotificationManager = mockk(relaxed = true)

        every { mockContext.applicationContext } returns mockContext
        every { mockContext.getSystemService(Context.NOTIFICATION_SERVICE) } returns mockNotificationManager
        
        // Handle android logs
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
        every { android.util.Log.w(any(), any<String>()) } returns 0

        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeResource(any(), any()) } returns mockk<Bitmap>(relaxed = true)
        
        mockkConstructor(NotificationCompat.Builder::class)
        every { anyConstructed<NotificationCompat.Builder>().build() } returns mockk<Notification>(relaxed = true)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `WateringWorker returns retry on exception`() = runTest {
        // En forçant une exception inattendue lors de l'accès aux tokens par exemple
        every { mockContext.applicationContext } throws RuntimeException("Token Datastore Crash")
        
        val worker = WateringWorker(mockContext, mockParams)
        val result = worker.doWork()

        assertTrue(result is Result.Retry)
    }
}
