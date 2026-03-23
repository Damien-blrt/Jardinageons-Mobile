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
import androidx.compose.ui.unit.dp
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
        title = { Text("Modifier") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), Arrangement.spacedBy(8.dp)) {
                InputComponent(value = name, label = "Nom", onValueChange = { name = it })
                InputComponent(
                    value = germination,
                    label = "Germination (jours)",
                    variant = InputType.NUMBER,
                    onValueChange = { germination = it })
                InputComponent(
                    value = sowingStart,
                    label = "Semis début",
                    variant = InputType.DATE,
                    onValueChange = { sowingStart = it })
                InputComponent(
                    value = sowingEnd,
                    label = "Semis fin",
                    variant = InputType.DATE,
                    onValueChange = { sowingEnd = it })
            }
        },
        confirmButton = {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                ButtonComponent(
                    onClick = { showConfirm = true },
                    label = "Supprimer",
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
                    label = "Enregistrer",
                    variant = ButtonVariant.PRIMARY
                )
            }
        },
        dismissButton = { TextButton(onDismiss) { Text("Annuler", color = Color.Gray) } }
    )

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Supprimer ?") },
            confirmButton = {
                ButtonComponent(
                    onClick = {
                        onDelete(vegetable.id)
                        showConfirm = false
                    },
                    label = "Confirmer",
                    variant = ButtonVariant.DANGER
                )
            },
            dismissButton = { TextButton({ showConfirm = false }) { Text("Non") } }
        )
    }
}