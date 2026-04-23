package app.jardinageons

import app.jardinageons.data.models.Tokens
import app.jardinageons.data.storage.TokenManager
import app.jardinageons.data.storage.TokenSerializer
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class TokenManagerTest {

    @Test
    fun `TokenManager variables hold values correctly`() {
        TokenManager.accessToken = "my_access_token"
        TokenManager.refreshToken = "my_refresh_token"

        assertEquals("my_access_token", TokenManager.accessToken)
        assertEquals("my_refresh_token", TokenManager.refreshToken)

        TokenManager.accessToken = null
        TokenManager.refreshToken = null

        assertNull(TokenManager.accessToken)
        assertNull(TokenManager.refreshToken)
    }

    @Test
    fun `TokenSerializer reads from string properly`() = runTest {
        val jsonString = """{"tokenType":"Bearer","accessToken":"abc","refreshToken":"def","expiresIn":3600}"""
        val stream = ByteArrayInputStream(jsonString.toByteArray())

        val tokens = TokenSerializer.readFrom(stream)

        assertEquals("Bearer", tokens?.tokenType)
        assertEquals("abc", tokens?.accessToken)
        assertEquals(3600L, tokens?.expiresIn)
        assertEquals("def", tokens?.refreshToken)
    }

    @Test
    fun `TokenSerializer writes to stream properly`() = runTest {
        val tokens = Tokens("Bearer", "abc", "def", 3600L)
        val outputStream = ByteArrayOutputStream()

        TokenSerializer.writeTo(tokens, outputStream)
        
        val writtenString = outputStream.toString("UTF-8")
        val expectedString = """{"tokenType":"Bearer","accessToken":"abc","refreshToken":"def","expiresIn":3600}"""
        
        assertEquals(expectedString, writtenString)
    }

    @Test
    fun `TokenSerializer default value is null`() {
        assertNull(TokenSerializer.defaultValue)
    }

    @Test
    fun `TokenSerializer writes empty to stream when null`() = runTest {
        val outputStream = ByteArrayOutputStream()
        TokenSerializer.writeTo(null, outputStream)
        assertEquals("", outputStream.toString("UTF-8"))
    }
}
