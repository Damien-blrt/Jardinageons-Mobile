package app.jardinageons.presentation.features.vegetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.jardinageons.R
import app.jardinageons.data.models.Vegetable
import app.jardinageons.presentation.components.ButtonComponent
import app.jardinageons.presentation.components.ButtonVariant
import app.jardinageons.presentation.components.InputComponent
import app.jardinageons.presentation.components.InputType
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVegetableModal(
    vegetable: Vegetable,
    onDismiss: () -> Unit,
    onSave: (Vegetable) -> Unit,
    onDelete: (Long) -> Unit
) {
    var name by remember { mutableStateOf(vegetable.name) }
    var description by remember { mutableStateOf(vegetable.description) }
    var germination by remember { mutableStateOf(vegetable.germinationTime.toString()) }
    var sowingStart by remember { mutableStateOf(vegetable.sowingStart) }
    var sowingEnd by remember { mutableStateOf(vegetable.sowingEnd) }
    var harvestStart by remember { mutableStateOf(vegetable.harvestStart) }
    var harvestEnd by remember { mutableStateOf(vegetable.harvestEnd) }
    var showConfirm by remember { mutableStateOf(false) }

    fun toIso(dateStr: String): String {
        return try {
            val inFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            outFmt.format(inFmt.parse(dateStr)!!)
        } catch (e: Exception) {
            dateStr
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.vegetable_edit_title)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), Arrangement.spacedBy(8.dp)) {
                InputComponent(value = name, label = stringResource(R.string.common_name), onValueChange = { name = it })
                InputComponent(
                    value = germination,
                    label = stringResource(R.string.vegetable_germination_label),
                    variant = InputType.NUMBER,
                    onValueChange = { germination = it })
                InputComponent(
                    value = sowingStart,
                    label = stringResource(R.string.vegetable_sowing_start_alt),
                    variant = InputType.DATE,
                    onValueChange = { sowingStart = it })
                InputComponent(
                    value = sowingEnd,
                    label = stringResource(R.string.vegetable_sowing_end_alt),
                    variant = InputType.DATE,
                    onValueChange = { sowingEnd = it })
            }
        },
        confirmButton = {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                ButtonComponent(
                    onClick = { showConfirm = true },
                    label = stringResource(R.string.common_delete),
                    variant = ButtonVariant.DANGER
                )
                ButtonComponent(
                    onClick = {
                        onSave(
                            vegetable.copy(
                                name = name,
                                description = description,
                                germinationTime = germination.toIntOrNull() ?: 0,
                                sowingStart = toIso(sowingStart),
                                sowingEnd = toIso(sowingEnd),
                                harvestStart = toIso(harvestStart),
                                harvestEnd = toIso(harvestEnd)
                            )
                        )
                    },
                    label = stringResource(R.string.common_save),
                    variant = ButtonVariant.PRIMARY
                )
            }
        },
        dismissButton = { TextButton(onDismiss) { Text(stringResource(R.string.common_cancel), color = Color.Gray) } }
    )

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text(stringResource(R.string.vegetable_delete_confirm_title)) },
            confirmButton = {
                ButtonComponent(
                    onClick = {
                        onDelete(vegetable.id)
                        showConfirm = false
                    },
                    label = stringResource(R.string.common_confirm),
                    variant = ButtonVariant.DANGER
                )
            },
            dismissButton = { TextButton({ showConfirm = false }) { Text(stringResource(R.string.common_no)) } }
        )
    }
}
