package app.jardinageons

import app.jardinageons.data.annotations.InjectAuth
import app.jardinageons.data.interceptors.AuthInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Invocation
import java.lang.reflect.Method

class AuthInterceptorTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var client: OkHttpClient
    private var tokenProviderResult: String? = null

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val interceptor = AuthInterceptor { tokenProviderResult }

        client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    interface DummyApi {
        @InjectAuth
        fun protectedMethod()
    }

    private fun getDummyMethod(): Method {
        return DummyApi::class.java.getMethod("protectedMethod")
    }

    @Test
    fun `intercept adds token when InjectAuth is present and token exists`() {
        tokenProviderResult = "valid_token_123"
        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        val invocation = Invocation.of(getDummyMethod(), listOf<Any>())
        
        val request = Request.Builder()
            .url(mockWebServer.url("/"))
            .tag(Invocation::class.java, invocation)
            .build()

        client.newCall(request).execute()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("Bearer valid_token_123", recordedRequest.getHeader("Authorization"))
    }

    @Test
    fun `intercept does not add token when token is null`() {
        tokenProviderResult = null
        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        val invocation = Invocation.of(getDummyMethod(), listOf<Any>())

        val request = Request.Builder()
            .url(mockWebServer.url("/"))
            .tag(Invocation::class.java, invocation)
            .build()

        client.newCall(request).execute()

        val recordedRequest = mockWebServer.takeRequest()
        assertNull(recordedRequest.getHeader("Authorization"))
    }

    @Test
    fun `intercept does not add token when InjectAuth is absent`() {
        tokenProviderResult = "valid_token_123"
        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        // No Invocation tag Means no InjectAuth
        val request = Request.Builder()
            .url(mockWebServer.url("/"))
            .build()

        client.newCall(request).execute()

        val recordedRequest = mockWebServer.takeRequest()
        assertNull(recordedRequest.getHeader("Authorization"))
    }
}
