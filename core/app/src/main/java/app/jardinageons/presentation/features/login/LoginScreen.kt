package app.jardinageons.presentation.features.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.jardinageons.R
import app.jardinageons.presentation.components.ButtonComponent
import app.jardinageons.presentation.components.ButtonVariant
import app.jardinageons.presentation.components.InputComponent
import app.jardinageons.presentation.components.InputType

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onRegisterClick: () -> Unit = {}
) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.login_welcome),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        InputComponent(
            value = login,
            label = stringResource(R.string.login_email),
            variant = InputType.CLASSIC,
            onValueChange = { login = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        InputComponent(
            value = password,
            label = stringResource(R.string.login_password),
            variant = InputType.CLASSIC,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp)
        )

        ButtonComponent(
            label = stringResource(R.string.login_button),
            variant = ButtonVariant.PRIMARY,
            onClick = { onLoginClick(login, password) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        ButtonComponent(
            label = stringResource(R.string.login_register_button),
            variant = ButtonVariant.SECONDARY,
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
