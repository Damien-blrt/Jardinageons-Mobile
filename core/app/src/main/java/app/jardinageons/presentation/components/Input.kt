package app.jardinageons.presentation.components

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import java.util.*

enum class InputType {
    CLASSIC,

    NUMBER,
    DATE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputComponent(
    value: String,
    label: String,
    variant: InputType = InputType.CLASSIC,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
) {

    when (variant) {

        InputType.CLASSIC -> {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(text = label) },
                modifier = modifier.onFocusChanged {
                    onFocusChanged(it.isFocused)
                }
            )
        }
        InputType.NUMBER->{
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                keyboardOptions = keyboardOptions,
                label = { Text(text = label) },
                modifier = modifier.onFocusChanged {
                    onFocusChanged(it.isFocused)
                }
            )

        }

        InputType.DATE -> {

            var showDatePicker by remember { mutableStateOf(false) }
            val datePickerState = rememberDatePickerState()

            OutlinedTextField(
                value = value,
                keyboardOptions =keyboardOptions,
                onValueChange = {},
                label = { Text(text = label) },
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color.Gray,
                    disabledLabelColor = Color.Gray
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val selectedDate =
                                datePickerState.selectedDateMillis?.let { millis ->
                                    SimpleDateFormat(
                                        "dd/MM/yyyy",
                                        Locale.getDefault()
                                    ).format(Date(millis))
                                } ?: ""

                            onValueChange(selectedDate)
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}