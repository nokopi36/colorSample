package com.nokopi.colorsample.ui.palette

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nokopi.colorsample.R

/**
 * 色グループの名前を入れるダイアログ。新規作成と名前の変更で共用する。
 *
 * 新規作成では最初の色も一緒に決める。色が0件のグループを存在させたくないので
 * （空のパレットは表示も操作もできない）、名前だけで作らせない。
 * 色の指定は既存の [ColorEditorDialog] を続けて出すことで済ませ、
 * ここでは名前だけを扱う。
 */
@Composable
fun PaletteNameDialog(
    title: String,
    confirmLabel: String,
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    val isBlank = name.isBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.palette_name)) },
                    singleLine = true,
                    isError = isBlank,
                    supportingText = if (isBlank) {
                        { Text(stringResource(R.string.palette_name_required)) }
                    } else {
                        null
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(R.string.palette_name_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name.trim()) }, enabled = !isBlank) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}
