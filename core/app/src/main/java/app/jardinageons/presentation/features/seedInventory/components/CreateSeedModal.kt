package app.jardinageons.presentation.features.seedInventory.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.jardinageons.presentation.components.ButtonComponent
import app.jardinageons.presentation.components.ButtonVariant
import app.jardinageons.presentation.features.seedInventory.SeedRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSeedModal(
    onDismiss: () -> Unit,
    onSave: (SeedRequest) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var germination by remember { mutableStateOf("") }
    var dateExpiration by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Créer une variété") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom") }
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantité") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = germination,
                    onValueChange = { germination = it },
                    label = { Text("Temps de germination (en jours)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                /**
                 * doc: https://developer.android.com/develop/ui/compose/components/datepickers?hl=fr
                 */
                OutlinedTextField(
                    value = dateExpiration,
                    onValueChange = { },
                    label = { Text("Date d'expiration") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = Color.Gray,
                        disabledLabelColor = Color.Gray
                    )
                )

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val selectedDate = datePickerState.selectedDateMillis?.let {
                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                                        Date(
                                            it
                                        )
                                    )
                                } ?: ""
                                dateExpiration = selectedDate
                                showDatePicker = false
                            }) { Text("OK") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }
        },
        confirmButton = {
            ButtonComponent(
                onClick = {
                    onSave(
                        SeedRequest(
                            name = name,
                            quantity = quantity.toIntOrNull() ?: 0,
                            germinationTime = germination.toIntOrNull() ?: 0,
                            description = "",
                            expiryDate = dateExpiration,
                            vegetableId = 1
                        )
                    )
                },
                label = "Créer",
            )

        },
        dismissButton = {
            ButtonComponent(
                onClick = {
                    onDismiss()
                },
                label = "Annuler",
                variant = ButtonVariant.SECONDARY
            )
        }
    )
}