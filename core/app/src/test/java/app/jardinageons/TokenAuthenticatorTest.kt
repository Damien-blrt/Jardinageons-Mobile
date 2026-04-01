package app.jardinageons

import app.jardinageons.data.interceptors.TokenAuthenticator
import app.jardinageons.data.models.LoginResponse
import app.jardinageons.data.models.RefreshRequest
import app.jardinageons.data.services.ILoginQService
import app.jardinageons.data.services.RetrofitClient
import app.jardinageons.data.storage.TokenManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TokenAuthenticatorTest {

    private lateinit var authenticator: TokenAuthenticator
    private lateinit var mockLoginService: ILoginQService

    @Before
    fun setup() {
        authenticator = TokenAuthenticator()
        mockLoginService = mockk()
        mockkObject(RetrofitClient)
        every { RetrofitClient.loginQService } returns mockLoginService
        TokenManager.refreshToken = "old_refresh_token"
    }

    @After
    fun teardown() {
        unmockkAll()
        TokenManager.accessToken = null
        TokenManager.refreshToken = null
    }

    private fun mockResponse(
        code: Int = 401,
        priorCount: Int = 1,
        url: String = "http://example.com"
    ): Response {
        val request = Request.Builder().url(url).build()
        var resp = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("Unauthorized")
            .build()
        
        repeat(priorCount - 1) {
            resp = resp.newBuilder().priorResponse(resp).build()
        }
        return resp
    }

    @Test
    fun `authenticate returns null if response count limits reached`() {
        val response = mockResponse(priorCount = 3)
        val result = authenticator.authenticate(null, response)
        assertNull(result)
    }

    @Test
    fun `authenticate returns null if refreshToken is missing`() {
        TokenManager.refreshToken = null
        val response = mockResponse()
        val result = authenticator.authenticate(null, response)
        assertNull(result)
    }

    @Test
    fun `authenticate returns null for refresh endpoint to avoid recursion`() {
        val response = mockResponse(
            url = "https://example.com/authentication/refresh"
        )

        val result = authenticator.authenticate(null, response)

        assertNull(result)
    }

    @Test
    fun `authenticate returns null if api throws exception`() {
        every { mockLoginService.refreshToken(any()) } throws RuntimeException("Network Error")
        val response = mockResponse()
        val result = authenticator.authenticate(null, response)
        assertNull(result)
    }

    @Test
    fun `authenticate gets new token and modifies request header on success`() {
        val newTokens = LoginResponse("Bearer", "new_access_token", 3600L, "new_refresh_token")
        val expectedRequest = RefreshRequest("old_refresh_token")
        
        val mockCall = mockk<retrofit2.Call<LoginResponse>>()
        every { mockCall.execute() } returns retrofit2.Response.success(newTokens)
        every { mockLoginService.refreshToken(expectedRequest) } returns mockCall

        val response = mockResponse()
        val result = authenticator.authenticate(null, response)

        assertEquals("new_access_token", TokenManager.accessToken)
        assertEquals("new_refresh_token", TokenManager.refreshToken)
        assertEquals("Bearer new_access_token", result?.header("Authorization"))
    }

    @Test
    fun `authenticate returns null if api returns error code`() {
        val expectedRequest = RefreshRequest("old_refresh_token")
        val errorResponse = okhttp3.Response.Builder()
            .request(Request.Builder().url("http://example.com").build())
            .protocol(Protocol.HTTP_1_1)
            .code(400)
            .message("Bad Request")
            .build()
            
        val mockCall = mockk<retrofit2.Call<LoginResponse>>()
        every { mockCall.execute() } returns retrofit2.Response.error(400, okhttp3.ResponseBody.create(null, ""))
        every { mockLoginService.refreshToken(expectedRequest) } returns mockCall

        val response = mockResponse()
        val result = authenticator.authenticate(null, response)

        assertNull(result)
    }
}
