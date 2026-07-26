package com.nokopi.colorsample.ui.palette

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import com.nokopi.colorsample.data.model.ColorOption
import com.nokopi.colorsample.data.model.Palette
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.model.resolve
import com.nokopi.colorsample.ui.device.ColorSwatch

/** 色のダイアログの状態。 */
private sealed interface ColorTarget {
    data class New(val paletteId: PaletteId) : ColorTarget
    data class Existing(val option: ColorOption) : ColorTarget

    /** 新しいグループの最初の色。名前を決めたあとに続けて出す。 */
    data class FirstOfNewPalette(val paletteName: String) : ColorTarget
}

/** グループ名のダイアログの状態。 */
private sealed interface PaletteTarget {
    data object New : PaletteTarget
    data class Rename(val palette: Palette) : PaletteTarget
}

/**
 * 全パレットを1つの LazyColumn に並べるので、行のキーには必ずパレットを混ぜる。
 *
 * 白や黒のような組み込みの色は複数のパレットに同じ [com.nokopi.colorsample.data.model.ColorId]
 * で登場するため、色の ID だけをキーにすると LazyColumn が重複キーで落ちる。
 */
internal fun colorItemKey(palette: Palette, option: ColorOption): String =
    "${palette.id.value}/${option.id.value}"

/**
 * 色の管理画面。
 *
 * 組み込みの色は消せないので「非表示」にする（グループ単位・元に戻せる）。
 * 色グループそのものも追加・改名でき、ユーザーが作ったグループは削除もできる。
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
    val undoable by viewModel.undoable.collectAsStateWithLifecycle()
    val paletteInUse by viewModel.paletteInUse.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var colorTarget by remember { mutableStateOf<ColorTarget?>(null) }
    var paletteTarget by remember { mutableStateOf<PaletteTarget?>(null) }

    // 配色画面の「色を追加」から来たときは、そのグループの追加ダイアログをすぐ開く。
    LaunchedEffect(focusPaletteId) {
        if (focusPaletteId != null) colorTarget = ColorTarget.New(focusPaletteId)
    }

    val deletedTemplate = stringResource(R.string.color_deleted)
    val hiddenTemplate = stringResource(R.string.color_hidden)
    val undoLabel = stringResource(R.string.undo)
    LaunchedEffect(undoable) {
        val target = undoable ?: return@LaunchedEffect
        val message = when (target) {
            is Undoable.ColorDeleted -> deletedTemplate.format(target.name)
            is Undoable.ColorHidden -> hiddenTemplate.format(target.name)
        }
        val result = snackbarHostState.showSnackbar(message = message, actionLabel = undoLabel)
        if (result == SnackbarResult.ActionPerformed) viewModel.undo() else viewModel.consumeUndoable()
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { paletteTarget = PaletteTarget.New },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.add_palette)) },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxWidth(),
            // FAB に最後の行が隠れないぶんの余白。
            contentPadding = PaddingValues(bottom = 88.dp),
        ) {
            palettes.forEach { palette ->
                item(key = "header-${palette.id.value}") {
                    PaletteHeader(
                        palette = palette,
                        onAddColor = { colorTarget = ColorTarget.New(palette.id) },
                        onRename = { paletteTarget = PaletteTarget.Rename(palette) },
                        onDelete = { viewModel.deletePalette(palette) },
                    )
                }

                items(palette.options, key = { colorItemKey(palette, it) }) { option ->
                    ColorRow(
                        option = option,
                        // 最後の1色は消せない。空のグループは表示も操作もできないため。
                        canRemove = palette.options.size > 1,
                        onEdit = { colorTarget = ColorTarget.Existing(option) },
                        onDelete = { name -> viewModel.deleteColor(option.id, name) },
                        onHide = { name -> viewModel.hideColor(palette.id, option.id, name) },
                    )
                }

                if (palette.hiddenOptions.isNotEmpty()) {
                    item(key = "hidden-${palette.id.value}") {
                        HiddenRow(
                            count = palette.hiddenOptions.size,
                            onRestore = { viewModel.unhideAll(palette.id) },
                        )
                    }
                }

                item(key = "divider-${palette.id.value}") { HorizontalDivider() }
            }

            // 「グループを追加」はここに置かない。
            // カタログが届く前は項目がこれ1件だけになり、LazyColumn がこのキーを
            // 先頭の基準として覚えてしまう。あとからグループが前に挿入されると
            // この項目を先頭に保とうとして最下部までスクロールする。FAB に出している。
        }
    }

    // ---- ダイアログ ----------------------------------------------------

    when (val target = paletteTarget) {
        null -> Unit

        PaletteTarget.New -> PaletteNameDialog(
            title = stringResource(R.string.add_palette),
            confirmLabel = stringResource(R.string.next),
            initialName = "",
            onDismiss = { paletteTarget = null },
            onConfirm = { name ->
                paletteTarget = null
                // 色が0件のグループを作らないため、続けて最初の色を決めさせる。
                colorTarget = ColorTarget.FirstOfNewPalette(name)
            },
        )

        is PaletteTarget.Rename -> PaletteNameDialog(
            title = stringResource(R.string.rename_palette),
            confirmLabel = stringResource(R.string.save_color),
            initialName = target.palette.label.resolve(),
            onDismiss = { paletteTarget = null },
            onConfirm = { name ->
                viewModel.renamePalette(target.palette.id, name)
                paletteTarget = null
            },
        )
    }

    when (val target = colorTarget) {
        null -> Unit

        is ColorTarget.New -> ColorEditorDialog(
            title = stringResource(R.string.add_color),
            initialName = "",
            initialColor = Color.White,
            onDismiss = { colorTarget = null },
            onConfirm = { name, color ->
                viewModel.addColor(target.paletteId, name, color)
                colorTarget = null
            },
        )

        is ColorTarget.Existing -> ColorEditorDialog(
            title = stringResource(R.string.edit_color),
            initialName = target.option.label.resolve(),
            initialColor = target.option.color,
            onDismiss = { colorTarget = null },
            onConfirm = { name, color ->
                viewModel.updateColor(target.option.id, name, color)
                colorTarget = null
            },
        )

        is ColorTarget.FirstOfNewPalette -> ColorEditorDialog(
            title = stringResource(R.string.first_color_of, target.paletteName),
            initialName = "",
            initialColor = Color.White,
            // ここで取り消すとグループも作らない。中途半端な空グループを残さない。
            onDismiss = { colorTarget = null },
            onConfirm = { name, color ->
                viewModel.addPalette(target.paletteName, name, color)
                colorTarget = null
            },
        )
    }

    paletteInUse?.let { inUse ->
        AlertDialog(
            onDismissRequest = viewModel::dismissPaletteInUse,
            title = {
                Text(
                    stringResource(
                        R.string.palette_in_use_title,
                        inUse.palette.label.resolve(),
                    ),
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.palette_in_use_message))
                    inUse.usages.forEach { usage ->
                        Text(
                            text = stringResource(
                                R.string.palette_in_use_entry,
                                usage.device.label.resolve(),
                                usage.part.label.resolve(),
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::dismissPaletteInUse) {
                    Text(stringResource(R.string.ok))
                }
            },
        )
    }
}

@Composable
private fun PaletteHeader(
    palette: Palette,
    onAddColor: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = palette.label.resolve(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onAddColor) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text(
                text = stringResource(R.string.add_color),
                modifier = Modifier.padding(start = 4.dp),
            )
        }
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.more_options),
                )
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.rename_palette)) },
                    onClick = {
                        menuExpanded = false
                        onRename()
                    },
                )
                // 組み込みグループは組み込み装具が参照しているので削除させない。
                if (palette.isUserDefined) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete_palette)) },
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

@Composable
private fun ColorRow(
    option: ColorOption,
    canRemove: Boolean,
    onEdit: () -> Unit,
    onDelete: (name: String) -> Unit,
    onHide: (name: String) -> Unit,
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

        if (option.isBuiltIn) {
            // 組み込みの色はコード上の定義を消せないので、一覧から外すだけ。
            TextButton(onClick = { onHide(name) }, enabled = canRemove) {
                Text(stringResource(R.string.hide_color))
            }
        } else {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_color))
            }
            IconButton(onClick = { onDelete(name) }, enabled = canRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_color),
                )
            }
        }
    }
}

@Composable
private fun HiddenRow(count: Int, onRestore: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.hidden_colors_count, count),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onRestore) {
            Text(stringResource(R.string.restore_hidden_colors))
        }
    }
}
