package app.jardinageons.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.jardinageons.presentation.theme.Blue

enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    DANGER,
}

@Composable
fun ButtonComponent(
    label: String,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    onClick: () -> Unit,
    modifier: Modifier? = null
) {
    Button(
        onClick = onClick,
        colors = when (variant) {
            ButtonVariant.PRIMARY -> ButtonDefaults.buttonColors(containerColor = Blue)
            ButtonVariant.SECONDARY -> ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ButtonVariant.DANGER -> ButtonDefaults.buttonColors(containerColor = Color.Red)
        },
        border = when (variant) {
            ButtonVariant.PRIMARY -> BorderStroke(0.dp, Color.Transparent)
            ButtonVariant.SECONDARY -> BorderStroke(1.dp, Color.Gray)
            ButtonVariant.DANGER -> BorderStroke(0.dp, Color.Transparent)
        },
        modifier = Modifier.then(modifier ?: Modifier)
    ) {
        Text(
            text = label, color = when (variant) {
                ButtonVariant.PRIMARY -> Color.White
                ButtonVariant.SECONDARY -> Color.Black
                ButtonVariant.DANGER -> Color.White
            }
        )
    }
}

