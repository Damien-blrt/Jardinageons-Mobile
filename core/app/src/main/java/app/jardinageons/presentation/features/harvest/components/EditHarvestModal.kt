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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.jardinageons.R
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
        title = { Text(stringResource(R.string.harvest_edit_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InputComponent(
                    value = quantity,
                    label = stringResource(R.string.harvest_quantity_label),
                    variant = InputType.NUMBER,
                    onValueChange = { quantity = it }
                )
                InputComponent(
                    value = description,
                    label = stringResource(R.string.common_description),
                    onValueChange = { description = it }
                )
                InputComponent(
                    value = date,
                    label = stringResource(R.string.harvest_date_label),
                    variant = InputType.DATE,
                    onValueChange = { date = it }
                )

                if (showDeleteConfirmation) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirmation = false },
                        title = { Text(stringResource(R.string.common_confirm_delete)) },
                        text = { Text(stringResource(R.string.harvest_delete_confirm_message)) },
                        confirmButton = {
                            ButtonComponent(
                                onClick = {
                                    showDeleteConfirmation = false
                                    onDelete(harvest.id)
                                },
                                label = stringResource(R.string.common_delete),
                                variant = ButtonVariant.DANGER
                            )
                        },
                        dismissButton = {
                            ButtonComponent(
                                onClick = {
                                    showDeleteConfirmation = false
                                },
                                label = stringResource(R.string.common_cancel),
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
                label = stringResource(R.string.common_save),
            )
        },
        dismissButton = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ButtonComponent(
                    onClick = { showDeleteConfirmation = true },
                    label = stringResource(R.string.common_delete),
                    variant = ButtonVariant.DANGER
                )
                ButtonComponent(
                    onClick = { onDismiss() },
                    label = stringResource(R.string.common_cancel),
                    variant = ButtonVariant.SECONDARY
                )
            }
        }
    )
}
