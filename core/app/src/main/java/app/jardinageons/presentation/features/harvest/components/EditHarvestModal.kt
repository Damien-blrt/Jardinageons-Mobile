package app.jardinageons.presentation.features.harvest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import app.jardinageons.data.models.Harvest
import app.jardinageons.presentation.components.ButtonComponent
import app.jardinageons.presentation.components.ButtonVariant
import app.jardinageons.presentation.components.InputComponent
import app.jardinageons.presentation.components.InputType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHarvestModal(
    harvest: Harvest,
    onDismiss: () -> Unit,
    onSave: (Harvest) -> Unit,
    onDelete: (Long) -> Unit
) {
    var quantity by remember { mutableStateOf(harvest.quantity.toString()) }
    var description by remember { mutableStateOf(harvest.description) }
    var date by remember { mutableStateOf(harvest.date) }

    var showDeleteConfirmation by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier la récolte") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InputComponent(
                    value = quantity,
                    label = "Quantité (kg)",
                    variant = InputType.NUMBER,
                    onValueChange = { quantity = it }
                )
                InputComponent(
                    value = description,
                    label = "Description",
                    onValueChange = { description = it }
                )
                InputComponent(
                    value = date,
                    label = "Date de récolte",
                    variant = InputType.DATE,
                    onValueChange = { date = it }
                )

                if (showDeleteConfirmation) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirmation = false },
                        title = { Text("Confirmer la suppression") },
                        text = { Text("Es-tu sûr de vouloir supprimer cette récolte ? Cette action est irréversible.") },
                        confirmButton = {
                            ButtonComponent(
                                onClick = {
                                    showDeleteConfirmation = false
                                    onDelete(harvest.id)
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
                        harvest.copy(
                            quantity = quantity.toIntOrNull() ?: 0,
                            description = description,
                            date = date
                        )
                    )
                },
                label = "Enregistrer",
            )
        },
        dismissButton = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ButtonComponent(
                    onClick = { showDeleteConfirmation = true },
                    label = "Supprimer",
                    variant = ButtonVariant.DANGER
                )
                ButtonComponent(
                    onClick = { onDismiss() },
                    label = "Annuler",
                    variant = ButtonVariant.SECONDARY
                )
            }
        }
    )
}
