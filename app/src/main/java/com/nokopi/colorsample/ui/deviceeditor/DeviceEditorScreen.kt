package com.nokopi.colorsample.ui.deviceeditor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokopi.colorsample.R
import com.nokopi.colorsample.data.ImageImport
import com.nokopi.colorsample.data.model.Palette
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.model.resolve
import com.nokopi.colorsample.ui.device.rememberPartPainter
import com.nokopi.colorsample.ui.theme.PreviewCanvas
import com.nokopi.colorsample.ui.theme.PreviewCanvasOutline

/**
 * 装具を作る・直す画面。
 *
 * パーツごとに1枚の透過PNGを重ねて描く仕組みなので、素材にも条件がある
 * （全レイヤーが同じ寸法、パーツ層はフラットなシルエット、線画は1枚だけ）。
 * 条件を外れた画像は取り込みの時点で理由付きで弾く。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceEditorScreen(
    onNavigateUp: () -> Unit,
    viewModel: DeviceEditorViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.events.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showImageHelp by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        // 写真ピッカーではなくファイルピッカーにしているのは、アルファチャンネルが必須で
        // JPEG を選ばせても意味がないため。
        ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris -> viewModel.importImages(uris) }

    val rejectionMessages = rejectionMessages()
    LaunchedEffect(event) {
        when (val current = event) {
            null -> Unit
            is EditorEvent.Rejected -> {
                snackbarHostState.showSnackbar(rejectionMessages(current.rejection))
                viewModel.consumeEvent()
            }
            EditorEvent.Saved -> {
                viewModel.consumeEvent()
                onNavigateUp()
            }
            EditorEvent.SaveFailed -> {
                snackbarHostState.showSnackbar(rejectionMessages(null))
                viewModel.consumeEvent()
            }
        }
    }

    fun attemptLeave() {
        if (state.layers.isEmpty() && state.name.isBlank()) {
            viewModel.discard()
            onNavigateUp()
        } else {
            showDiscardDialog = true
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (state.isNew) R.string.add_device else R.string.edit_device,
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = ::attemptLeave) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::save, enabled = state.canSave) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.save_device),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "name") {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::updateName,
                    label = { Text(stringResource(R.string.device_name)) },
                    singleLine = true,
                    isError = state.name.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (state.layers.isNotEmpty()) {
                item(key = "preview") { DraftPreview(state) }
            }

            item(key = "pick") {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = { picker.launch(arrayOf(PNG_MIME_TYPE)) },
                            enabled = !state.isImporting,
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text(
                                text = stringResource(R.string.pick_part_images),
                                modifier = Modifier.padding(start = 4.dp),
                            )
                        }
                        // 詳しい説明は開いたときだけ出す。ここに全部書くと読まれない。
                        TextButton(onClick = { showImageHelp = true }) {
                            Text(stringResource(R.string.how_to_make_images))
                        }
                    }
                    Text(
                        text = stringResource(R.string.part_image_requirements),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (state.isImporting) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            itemsIndexed(state.layers, key = { _, layer -> layer.partId.value }) { index, layer ->
                LayerCard(
                    layer = layer,
                    index = index,
                    total = state.layers.size,
                    palettes = state.palettes,
                    onNameChange = { viewModel.updateLayerName(layer.partId, it) },
                    onPaletteChange = { viewModel.updateLayerPalette(layer.partId, it) },
                    onToggleTinted = { viewModel.toggleTinted(layer.partId) },
                    onMoveUp = { viewModel.moveLayer(layer.partId, -1) },
                    onMoveDown = { viewModel.moveLayer(layer.partId, 1) },
                    onRemove = { viewModel.removeLayer(layer.partId) },
                )
            }
        }
    }

    if (showImageHelp) {
        ImageHelpDialog(onDismiss = { showImageHelp = false })
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(stringResource(R.string.discard_changes_title)) },
            text = { Text(stringResource(R.string.discard_changes_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        viewModel.discard()
                        onNavigateUp()
                    },
                ) { Text(stringResource(R.string.discard)) }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

/**
 * パーツ画像の作り方。
 *
 * 使い方を別に用意せずアプリ内で完結させたいので、必要なことはここに全部書く。
 * 一方でボタンの下に全文を出すと読まれないため、短い案内だけ常に見せ、
 * 詳細は開いたときに出す形にしている。
 */
@Composable
private fun ImageHelpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.image_help_title)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(stringResource(R.string.image_help_intro))

                ImageHelpIllustration()

                HelpStep(R.string.image_help_step1_title, R.string.image_help_step1)
                HelpStep(R.string.image_help_step2_title, R.string.image_help_step2)
                HelpStep(R.string.image_help_step3_title, R.string.image_help_step3)
                HelpStep(R.string.image_help_step4_title, R.string.image_help_step4)
                HelpStep(R.string.image_help_step5_title, R.string.image_help_step5)

                HorizontalDivider()
                Text(
                    text = stringResource(R.string.image_help_format),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.image_help_order),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.ok)) }
        },
    )
}

/**
 * 「1枚の絵を部分ごとに分ける」を図で見せる。
 *
 * 文章だけだと伝わりにくいところなので、組み込みAブレースの実画像をそのまま使う。
 * パーツ層は白いシルエットなので、そのままでは白地に見えない。ここでは仕組みが分かるよう
 * わざと別々の色で塗って並べ、最後に同じ4枚を重ねた結果を出している。
 * 新しい画像アセットは足していない。
 */
@Composable
private fun ImageHelpIllustration(modifier: Modifier = Modifier) {
    // 面積のあるパーツを選ぶ。ボタンや糸のような細かい層はこの大きさでは見えない。
    val samples = listOf(
        Triple(R.drawable.a1, R.string.plastic, PART_SAMPLE_PLASTIC),
        Triple(R.drawable.a2, R.string.sponge, PART_SAMPLE_SPONGE),
        Triple(R.drawable.a3, R.string.belt1, PART_SAMPLE_BELT),
        Triple(R.drawable.a11, R.string.line_art, null),
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.image_help_parts_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            samples.forEach { (image, labelRes, tint) ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    HelpImageBox(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                        HelpLayer(image, tint)
                    }
                    Text(
                        text = stringResource(labelRes),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.image_help_stacked_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        HelpImageBox(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally),
        ) {
            // 上で見せた4枚をそのまま重ねる。並びが描画順で、線画が一番手前。
            samples.forEach { (image, _, tint) -> HelpLayer(image, tint) }
        }
    }
}

/** 図の1コマ。配色プレビューと同じ下地にして、同じものを見ていると分かるようにする。 */
@Composable
private fun HelpImageBox(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(PreviewCanvas)
            .border(1.dp, PreviewCanvasOutline, RoundedCornerShape(6.dp))
            .padding(2.dp),
        content = content,
    )
}

@Composable
private fun HelpLayer(image: Int, tint: Color?) {
    Image(
        painter = painterResource(image),
        contentDescription = null,
        colorFilter = tint?.let(ColorFilter::tint),
        contentScale = ContentScale.Fit,
        modifier = Modifier.fillMaxSize(),
    )
}

// 図のためだけの色。仕組みが分かるよう互いに離れた色を選んでいる。
private val PART_SAMPLE_PLASTIC = Color(0xFFF19CA7)
private val PART_SAMPLE_SPONGE = Color(0xFF7FFFD4)
private val PART_SAMPLE_BELT = Color(0xFF87CEFA)

@Composable
private fun HelpStep(titleRes: Int, bodyRes: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(stringResource(bodyRes), style = MaterialTheme.typography.bodyMedium)
    }
}

/** 組み立て中の見え方。色はパレットの先頭で仮に塗る。 */
@Composable
private fun DraftPreview(state: DeviceEditorUiState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(PreviewCanvas)
            .border(1.dp, PreviewCanvasOutline, RoundedCornerShape(12.dp))
            .padding(8.dp),
    ) {
        val paletteById = state.palettes.associateBy { it.id }
        // 一覧の並びがそのまま描画順。色を変えないレイヤーも位置どおりに描く。
        state.layers.forEach { layer ->
            val color = layer.paletteId?.let { paletteById[it] }?.options?.firstOrNull()?.color
            Image(
                painter = rememberPartPainter(layer.image),
                contentDescription = null,
                colorFilter = color?.let(ColorFilter::tint),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun LayerCard(
    layer: DraftLayer,
    index: Int,
    total: Int,
    palettes: List<Palette>,
    onNameChange: (String) -> Unit,
    onPaletteChange: (PaletteId) -> Unit,
    onToggleTinted: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Image(
                    painter = rememberPartPainter(layer.image),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(PreviewCanvas),
                )

                Text(
                    // 先頭が最背面。並びが描画順であることを数字で示す。
                    text = stringResource(R.string.layer_position, index + 1, total),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                )

                IconButton(onClick = onMoveUp, enabled = index > 0) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.move_layer_up),
                    )
                }
                IconButton(onClick = onMoveDown, enabled = index < total - 1) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.move_layer_down),
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.remove_layer),
                    )
                }
            }

            FilterChip(
                selected = !layer.isTinted,
                onClick = onToggleTinted,
                label = { Text(stringResource(R.string.dont_tint_layer)) },
            )

            // 色を変えないレイヤーは名前もパレットも要らない。
            if (layer.isTinted) {
                OutlinedTextField(
                    value = layer.name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.part_name)) },
                    placeholder = { Text(stringResource(R.string.part_name_hint)) },
                    singleLine = true,
                    isError = layer.name.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                )
                PaletteSelector(
                    selectedId = requireNotNull(layer.paletteId),
                    palettes = palettes,
                    onSelect = onPaletteChange,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaletteSelector(
    selectedId: PaletteId,
    palettes: List<Palette>,
    onSelect: (PaletteId) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = palettes.firstOrNull { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selected?.label?.resolve().orEmpty(),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(stringResource(R.string.part_palette)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            palettes.forEach { palette ->
                DropdownMenuItem(
                    text = { Text(palette.label.resolve()) },
                    onClick = {
                        onSelect(palette.id)
                        expanded = false
                    },
                )
            }
        }
    }
}

/** 弾いた理由を文言にする。何が悪いか分からないと直せないので、寸法は実際の値を出す。 */
@Composable
private fun rejectionMessages(): (ImageImport.Rejection?) -> String {
    val notPng = stringResource(R.string.reject_not_transparent_png)
    val empty = stringResource(R.string.reject_empty)
    val unreadable = stringResource(R.string.reject_unreadable)
    val saveFailed = stringResource(R.string.device_save_failed)
    val mismatch = stringResource(R.string.reject_size_mismatch)

    return { rejection ->
        when (rejection) {
            null -> saveFailed
            ImageImport.Rejection.NotTransparentPng -> notPng
            ImageImport.Rejection.Empty -> empty
            ImageImport.Rejection.Unreadable -> unreadable
            is ImageImport.Rejection.SizeMismatch ->
                mismatch.format(rejection.expected.toString(), rejection.actual.toString())
        }
    }
}

private const val PNG_MIME_TYPE = "image/png"
