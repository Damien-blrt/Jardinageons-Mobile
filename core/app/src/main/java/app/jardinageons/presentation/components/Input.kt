package app.jardinageons.presentation.components

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.Placeholder

/**
 * doc :https://stackoverflow.com/questions/58883218/textfield-with-hint-text-in-jetpack-compose
 */
@Composable
fun InputComponent(
    label: String,
    placeholder: Placeholder,
    onValueChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit = {}

){
    TextField(
        label = label,
        placeholder=placeholder,
        onValueChange = onValueChange,
        modifier = Modifier.onFocusChanged {
            onFocusChanged(it.isFocused)
        }
    )


}