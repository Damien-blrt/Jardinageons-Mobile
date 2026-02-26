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
import app.jardinageons.data.models.Seed
import app.jardinageons.presentation.components.ButtonComponent
import app.jardinageons.presentation.components.ButtonVariant
import app.jardinageons.presentation.components.InputComponent
import app.jardinageons.presentation.components.InputType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * doc: https://developer.android.com/reference/kotlin/androidx/compose/material3/ExperimentalMaterial3Api
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSeedModal(
    seed: Seed,
    onDismiss: () -> Unit,
    onSave: (Seed) -> Unit,
    onDelete: (Long) -> Unit
) {
    var name by remember { mutableStateOf(seed.name) }
    var quantity by remember { mutableStateOf(seed.quantity.toString()) }
    var germination by remember { mutableStateOf(seed.germinationTime.toString()) }
    var dateExpiration by remember { mutableStateOf(seed.expiryDate) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var showDeleteConfirmation by remember { mutableStateOf(false) }

    /**
     * doc: https://developer.android.com/develop/ui/compose/components/dialog?hl=fr
     */
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier la variété") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InputComponent(
                    value = name,
                    label = "Nom de Légume",
                    onValueChange = { name = it }
                )
                InputComponent(
                    value = quantity,
                    label = "Quantité",
                    onValueChange = { quantity = it }
                )
                InputComponent(
                    value = germination,
                    label = "Temps de germination(en nombre de jours)",
                    onValueChange = { germination = it }
                )
                InputComponent(
                    value = dateExpiration,
                    label = "Date d'expiration",
                    variant = InputType.DATE,
                    onValueChange = { dateExpiration = it }
                )

                if (showDeleteConfirmation) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirmation = false },
                        title = { Text("Confirmer la suppression") },
                        text = { Text("Es-tu sûr de vouloir supprimer la variété $name ? Cette action est irréversible.") },
                        confirmButton = {
                            ButtonComponent(
                                onClick = {
                                    showDeleteConfirmation = false
                                    onDelete(seed.id)
                                },
                                label = "Supprimer",
                                variant = ButtonVariant.DANGER
                            )
                        },
                        dismissButton = {
                            ButtonComponent(
                                onClick = {
                                    showDeleteConfirmation = false
                                },
                                label = "Annuler",
                                variant = ButtonVariant.SECONDARY
                            )
                        }
                    )
                }
            }
        },
        confirmButton = {
            ButtonComponent(
                onClick = {
                    onSave(
                        seed.copy(
                            name = name,
                            quantity = quantity.toIntOrNull() ?: 0,
                            germinationTime = germination.toIntOrNull() ?: 0,
                            expiryDate = dateExpiration
                        )
                    )
                },
                label = "Enregistrer",
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