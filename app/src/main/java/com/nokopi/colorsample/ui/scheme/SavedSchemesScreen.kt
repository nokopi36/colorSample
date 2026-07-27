package com.nokopi.colorsample.ui.scheme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokopi.colorsample.R
import com.nokopi.colorsample.data.model.SavedScheme
import com.nokopi.colorsample.data.model.resolve
import com.nokopi.colorsample.ui.device.ColorSwatch
import com.nokopi.colorsample.ui.device.rememberPartPainter

/**
 * 保存した配色の一覧。
 *
 * 画像として書き出すと色名が残らないので、ここではパーツごとの色名まで文字で出す。
 * タップするとその配色で配色画面が開く。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedSchemesScreen(
    onNavigateUp: () -> Unit,
    onOpenScheme: (SavedScheme) -> Unit,
    viewModel: SavedSchemesViewModel,
    modifier: Modifier = Modifier,
) {
    val schemes by viewModel.schemes.collectAsStateWithLifecycle()

    var pendingDelete by remember { mutableStateOf<SavedScheme?>(null) }
    var pendingRename by remember { mutableStateOf<SavedScheme?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.saved_schemes)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            if (schemes.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_saved_schemes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            schemes.forEach { scheme ->
                SchemeCard(
                    scheme = scheme,
                    onClick = { onOpenScheme(scheme) },
                    onRename = { pendingRename = scheme },
                    onDelete = { pendingDelete = scheme },
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    pendingRename?.let { scheme ->
        SchemeNameDialog(
            title = stringResource(R.string.rename_scheme),
            initialName = scheme.name,
            onConfirm = { name ->
                pendingRename = null
                viewModel.rename(scheme.id, name)
            },
            onDismiss = { pendingRename = null },
        )
    }

    pendingDelete?.let { scheme ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.delete_scheme_title, scheme.name)) },
            text = { Text(stringResource(R.string.delete_scheme_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDelete = null
                        viewModel.delete(scheme.id)
                    },
                ) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun SchemeCard(
    scheme: SavedScheme,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(start = 16.dp, top = 8.dp, end = 4.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                painter = rememberPartPainter(scheme.device.thumbnail),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(72.dp),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(text = scheme.name, style = MaterialTheme.typography.titleMedium)

                // 装具名と氏名。氏名は空のことがあるので、あるときだけ足す。
                val subtitle = scheme.device.label.resolve() +
                    scheme.personName.takeIf { it.isNotBlank() }?.let { " ・ $it" }.orEmpty()
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(4.dp))

                // ここが一覧の主目的。どのパーツを何色にしたかを文字で残す。
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    scheme.selections.forEach { selection ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            ColorSwatch(color = selection.option.color, size = 14.dp)
                            Text(
                                text = stringResource(
                                    R.string.scheme_part_color,
                                    selection.part.label.resolve(),
                                    selection.option.label.resolve(),
                                ),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.more_options),
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.rename_scheme)) },
                        onClick = {
                            menuExpanded = false
                            onRename()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete_scheme)) },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        },
                    )
                }
            }
        }
    }
}

/**
 * 配色の名前を入れるダイアログ。保存と改名で共用する。
 *
 * 名前が空のままでは確定できない。一覧で見分けが付かなくなるため。
 */
@Composable
fun SchemeNameDialog(
    title: String,
    initialName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.scheme_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank(),
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}
