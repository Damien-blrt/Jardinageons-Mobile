package app.jardinageons.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.jardinageons.data.storage.TokenManager
import app.jardinageons.data.storage.tokenDataStore

@Composable
fun AuthorizeView(
    authorized: @Composable () -> Unit,
    unauthorized: @Composable () -> Unit
) {
    val context = LocalContext.current
    val tokenFlow = context.tokenDataStore.data.collectAsStateWithLifecycle(initialValue = null)
    
    val tokens = tokenFlow.value
    TokenManager.accessToken = tokens?.accessToken
    TokenManager.refreshToken = tokens?.refreshToken

    if (tokens?.accessToken.isNullOrEmpty()) {
        unauthorized()
    } else {
        authorized()
    }
}
