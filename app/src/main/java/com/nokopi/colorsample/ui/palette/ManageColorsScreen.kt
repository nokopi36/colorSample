package com.nokopi.colorsample.ui.palette

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokopi.colorsample.R
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.ColorOption
import com.nokopi.colorsample.data.model.Palette
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.model.resolve
import com.nokopi.colorsample.ui.device.ColorSwatch

/** 編集中のダイアログの状態。 */
private sealed interface EditorTarget {
    data class New(val paletteId: PaletteId) : EditorTarget
    data class Existing(val option: ColorOption) : EditorTarget
}

/**
 * 全パレットを1つの LazyColumn に並べるので、行のキーには必ずパレットを混ぜる。
 *
 * 白や黒のような組み込みの色は複数のパレットに同じ [ColorId] で登場するため、
 * 色の ID だけをキーにすると LazyColumn が重複キーで落ちる。
 */
internal fun colorItemKey(palette: Palette, option: ColorOption): String =
    "${palette.id.value}/${option.id.value}"

/**
 * 色の管理画面。パレット（素材の区分）ごとに色を並べ、ユーザーが追加したものだけ編集・削除できる。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageColorsScreen(
    onNavigateUp: () -> Unit,
    viewModel: ManageColorsViewModel,
    focusPaletteId: PaletteId? = null,
    modifier: Modifier = Modifier,
) {
    val palettes by viewModel.palettes.collectAsStateWithLifecycle()
    val lastDeleted by viewModel.lastDeleted.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var editorTarget by remember { mutableStateOf<EditorTarget?>(null) }
    // 配色画面の「色を追加」から来たときは、そのパレットの追加ダイアログをすぐ開く。
    LaunchedEffect(focusPaletteId) {
        if (focusPaletteId != null) editorTarget = EditorTarget.New(focusPaletteId)
    }

    val deletedTemplate = stringResource(R.string.color_deleted)
    val undoLabel = stringResource(R.string.undo)
    LaunchedEffect(lastDeleted) {
        val deleted = lastDeleted ?: return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = deletedTemplate.format(deleted.name),
            actionLabel = undoLabel,
        )
        if (result == SnackbarResult.ActionPerformed) {
            viewModel.undoDelete()
        } else {
            viewModel.consumeDeleted()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.manage_colors)) },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            palettes.forEach { palette ->
                item(key = "header-${palette.id.value}") {
                    PaletteHeader(
                        palette = palette,
                        onAdd = { editorTarget = EditorTarget.New(palette.id) },
                    )
                }
                items(palette.options, key = { colorItemKey(palette, it) }) { option ->
                    ColorRow(
                        option = option,
                        onEdit = { editorTarget = EditorTarget.Existing(option) },
                        onDelete = { viewModel.deleteColor(option.id, it) },
                    )
                }
                item(key = "divider-${palette.id.value}") { HorizontalDivider() }
            }
        }
    }

    when (val target = editorTarget) {
        null -> Unit

        is EditorTarget.New -> ColorEditorDialog(
            title = stringResource(R.string.add_color),
            initialName = "",
            initialColor = Color.White,
            onDismiss = { editorTarget = null },
            onConfirm = { name, color ->
                viewModel.addColor(target.paletteId, name, color)
                editorTarget = null
            },
        )

        is EditorTarget.Existing -> ColorEditorDialog(
            title = stringResource(R.string.edit_color),
            initialName = target.option.label.resolve(),
            initialColor = target.option.color,
            onDismiss = { editorTarget = null },
            onConfirm = { name, color ->
                viewModel.updateColor(target.option.id, name, color)
                editorTarget = null
            },
        )
    }
}

@Composable
private fun PaletteHeader(palette: Palette, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = palette.label.resolve(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onAdd) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text(
                text = stringResource(R.string.add_color),
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

@Composable
private fun ColorRow(
    option: ColorOption,
    onEdit: () -> Unit,
    onDelete: (name: String) -> Unit,
) {
    val name = option.label.resolve()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ColorSwatch(option.color, size = 28.dp)

        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyLarge)
            if (option.isBuiltIn) {
                Text(
                    text = stringResource(R.string.built_in_color),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // 組み込みの色は編集も削除もできないので、ボタン自体を出さない。
        if (!option.isBuiltIn) {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_color))
            }
            IconButton(onClick = { onDelete(name) }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_color),
                )
            }
        }
    }
}
