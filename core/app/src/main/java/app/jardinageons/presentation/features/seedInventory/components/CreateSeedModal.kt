package app.jardinageons.presentation.features.seedInventory.components

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
import app.jardinageons.data.models.SeedRequest
import app.jardinageons.presentation.components.ButtonComponent
import app.jardinageons.presentation.components.ButtonVariant
import app.jardinageons.presentation.components.InputComponent
import app.jardinageons.presentation.components.InputType

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
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.seed_create_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InputComponent(
                    value = name,
                    label = stringResource(R.string.seed_name_label),
                    onValueChange = { name = it }
                )
                InputComponent(
                    value = quantity,
                    label = stringResource(R.string.common_quantity),
                    variant = InputType.NUMBER,
                    onValueChange = { quantity = it }
                )
                InputComponent(
                    value = germination,
                    label = stringResource(R.string.seed_germination_label),
                    variant = InputType.NUMBER,
                    onValueChange = { germination = it }
                )
                InputComponent(
                    value = dateExpiration,
                    label = stringResource(R.string.seed_expiry_label),
                    variant = InputType.DATE,
                    onValueChange = { dateExpiration = it }
                )
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
                label = stringResource(R.string.common_create),
            )

        },
        dismissButton = {
            ButtonComponent(
                onClick = {
                    onDismiss()
                },
                label = stringResource(R.string.common_cancel),
                variant = ButtonVariant.SECONDARY
            )
        }
    )
}
