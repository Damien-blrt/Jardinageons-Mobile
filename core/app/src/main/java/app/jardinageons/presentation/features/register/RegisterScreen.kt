package app.jardinageons.presentation.features.register

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.jardinageons.presentation.components.ButtonComponent
import app.jardinageons.presentation.components.ButtonVariant
import app.jardinageons.presentation.components.InputComponent
import app.jardinageons.presentation.components.InputType

@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: (String, String) -> Unit = { _, _ -> }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Créer un compte",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        InputComponent(
            value = email,
            label = "Adresse email",
            variant = InputType.CLASSIC,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        InputComponent(
            value = password,
            label = "Mot de passe",
            variant = InputType.CLASSIC,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        InputComponent(
            value = confirmPassword,
            label = "Confirmer le mot de passe",
            variant = InputType.CLASSIC,
            onValueChange = { confirmPassword = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp)
        )

        ButtonComponent(
            label = "S'inscrire",
            variant = ButtonVariant.PRIMARY,
            onClick = {
                if (password == confirmPassword && email.isNotEmpty() && password.isNotEmpty()) {
                    onRegisterClick(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        ButtonComponent(
            label = "J'ai déjà un compte",
            variant = ButtonVariant.SECONDARY,
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
