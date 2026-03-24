package app.jardinageons.data.storage

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.MultiProcessDataStoreFactory
import androidx.datastore.core.Serializer
import app.jardinageons.data.models.Tokens
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import android.content.Context
import androidx.datastore.dataStore
import java.io.File
import java.io.InputStream
import java.io.OutputStream

val Context.tokenDataStore by dataStore(
    fileName = "tokens.pb",
    serializer = TokenSerializer
)

object TokenSerializer : Serializer<Tokens?> {

    override val defaultValue: Tokens? = null

    override suspend fun readFrom(input: InputStream): Tokens? =
        try {
            Json.decodeFromString<Tokens>(
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Tokens", serialization)
        }

    override suspend fun writeTo(t: Tokens?, output: OutputStream) {
        if (t != null) {
            output.write(
                Json.encodeToString(t)
                    .encodeToByteArray()
            )
        } else {
        }
    }
}

object TokenManager {
    @Volatile
    var accessToken: String? = null

    @Volatile
    var refreshToken: String? = null
}