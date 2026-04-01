package app.jardinageons.presentation.features.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.jardinageons.data.models.LoginRequest
import app.jardinageons.data.models.Tokens
import app.jardinageons.data.services.RetrofitClient
import app.jardinageons.data.storage.TokenManager
import app.jardinageons.data.storage.tokenDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    object RegisterSuccess : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val response = RetrofitClient.loginQService.login(
                    LoginRequest(email = email, password = password)
                )
                if (response.isSuccessful) {
                    val loginData = response.body()
                    if (loginData != null) {
                        TokenManager.accessToken = loginData.accessToken
                        TokenManager.refreshToken = loginData.refreshToken
                        getApplication<Application>().tokenDataStore.updateData {
                            Tokens(
                                tokenType = loginData.tokenType,
                                accessToken = loginData.accessToken,
                                expiresIn = loginData.expiresIn,
                                refreshToken = loginData.refreshToken
                            )
                        }
                        _state.value = AuthState.Idle
                    }
                } else {
                    _state.value = AuthState.Error("Erreur de connexion : ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error("Erreur réseau : ${e.localizedMessage}")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val response = RetrofitClient.loginQService.register(
                    LoginRequest(email = email, password = password)
                )
                if (response.isSuccessful) {
                    _state.value = AuthState.RegisterSuccess
                } else {
                    _state.value = AuthState.Error("Erreur d'inscription : ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error("Erreur réseau : ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }
}
