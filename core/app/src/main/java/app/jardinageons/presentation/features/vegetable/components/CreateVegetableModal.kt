package app.jardinageons.presentation.features.vegetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.jardinageons.R
import app.jardinageons.presentation.components.ButtonComponent
import app.jardinageons.presentation.components.ButtonVariant
import app.jardinageons.presentation.components.InputComponent
import app.jardinageons.presentation.components.InputType
import app.jardinageons.presentation.features.vegetable.VegetableRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVegetableModal(onDismiss: () -> Unit, onSave: (VegetableRequest) -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var germination by remember { mutableStateOf("") }
    var sowingStart by remember { mutableStateOf("") }
    var sowingEnd by remember { mutableStateOf("") }
    var harvestStart by remember { mutableStateOf("") }
    var harvestEnd by remember { mutableStateOf("") }

    fun toIso(dateStr: String): String {
        return try {
            val inFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            outFmt.format(inFmt.parse(dateStr)!!)
        } catch (e: Exception) {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.vegetable_create_title)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), Arrangement.spacedBy(8.dp)) {
                InputComponent(value = name, label = stringResource(R.string.common_name), onValueChange = { name = it })
                InputComponent(
                    value = description,
                    label = stringResource(R.string.common_description),
                    onValueChange = { description = it })
                InputComponent(
                    value = germination,
                    label = stringResource(R.string.vegetable_germination_label),
                    variant = InputType.NUMBER,
                    onValueChange = { germination = it })
                InputComponent(
                    value = sowingStart,
                    label = stringResource(R.string.vegetable_sowing_start),
                    variant = InputType.DATE,
                    onValueChange = { sowingStart = it })
                InputComponent(
                    value = sowingEnd,
                    label = stringResource(R.string.vegetable_sowing_end),
                    variant = InputType.DATE,
                    onValueChange = { sowingEnd = it })
                InputComponent(
                    value = harvestStart,
                    label = stringResource(R.string.vegetable_harvest_start),
                    variant = InputType.DATE,
                    onValueChange = { harvestStart = it })
                InputComponent(
                    value = harvestEnd,
                    label = stringResource(R.string.vegetable_harvest_end),
                    variant = InputType.DATE,
                    onValueChange = { harvestEnd = it })
            }
        },
        confirmButton = {
            ButtonComponent(
                onClick = {
                    onSave(
                        VegetableRequest(
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
                label = stringResource(R.string.common_create)
            )
        },
        dismissButton = {
            ButtonComponent(
                onClick = onDismiss,
                label = stringResource(R.string.common_cancel),
                variant = ButtonVariant.SECONDARY
            )
        }
    )
}
