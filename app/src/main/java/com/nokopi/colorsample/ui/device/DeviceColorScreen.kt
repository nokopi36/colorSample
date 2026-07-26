package com.nokopi.colorsample.ui.device

import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokopi.colorsample.R
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.model.resolve
import com.nokopi.colorsample.util.ImageExport
import kotlinx.coroutines.launch

/**
 * 装具の配色画面。どの装具でもこの1画面で担い、違いはカタログの定義から読む。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceColorScreen(
    onNavigateUp: () -> Unit,
    onAddColor: (PaletteId) -> Unit,
    viewModel: DeviceColorViewModel,
    modifier: Modifier = Modifier,
) {
    when (val state = viewModel.uiState.collectAsStateWithLifecycle().value) {
        DeviceColorUiState.Loading -> LoadingScreen(modifier)

        // 表示中に装具が削除された場合。留まっても操作できないので戻す。
        DeviceColorUiState.NotFound -> LaunchedEffect(Unit) { onNavigateUp() }

        is DeviceColorUiState.Ready -> DeviceColorContent(
            state = state,
            onNavigateUp = onNavigateUp,
            onAddColor = onAddColor,
            viewModel = viewModel,
            modifier = modifier,
        )
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceColorContent(
    state: DeviceColorUiState.Ready,
    onNavigateUp: () -> Unit,
    onAddColor: (PaletteId) -> Unit,
    viewModel: DeviceColorViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val graphicsLayer = rememberGraphicsLayer()

    val deviceLabel = state.device.label.resolve()
    val savedMessage = stringResource(R.string.save_succeeded)
    val saveFailedMessage = stringResource(R.string.save_failed)
    val shareFailedMessage = stringResource(R.string.share_failed)
    val permissionDeniedMessage = stringResource(R.string.storage_permission_denied)
    val chooserTitle = stringResource(R.string.share_chooser_title)

    fun exportFileName() = ImageExport.buildFileName(deviceLabel, state.personName)

    fun save() {
        scope.launch {
            val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
            ImageExport.saveToGallery(context, bitmap, exportFileName())
                .onSuccess { snackbarHostState.showSnackbar(savedMessage) }
                .onFailure { snackbarHostState.showSnackbar(saveFailedMessage) }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            save()
        } else {
            scope.launch { snackbarHostState.showSnackbar(permissionDeniedMessage) }
        }
    }

    fun onSaveClick() {
        // API 29 未満だけ WRITE_EXTERNAL_STORAGE が要る。それ以降は MediaStore 経由で権限不要。
        val permission = ImageExport.legacyWritePermission
        if (permission == null ||
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        ) {
            save()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    fun onShareClick() {
        scope.launch {
            val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
            ImageExport.createShareIntent(context, bitmap, exportFileName())
                .onSuccess { context.startActivity(Intent.createChooser(it, chooserTitle)) }
                .onFailure { snackbarHostState.showSnackbar(shareFailedMessage) }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(deviceLabel) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::reset) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.reset),
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
        BoxWithConstraints(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            val isWide = maxWidth > maxHeight
            val controlPaneWidth = if (isWide) maxWidth / 2 else maxWidth
            val columns = if (controlPaneWidth >= 320.dp) 2 else 1

            val preview: @Composable (Modifier) -> Unit = { previewModifier ->
                ColorPreview(
                    deviceLabel = deviceLabel,
                    parts = state.parts,
                    overlay = state.device.overlay,
                    personName = state.personName,
                    graphicsLayer = graphicsLayer,
                    modifier = previewModifier,
                )
            }

            val controls: @Composable (Modifier) -> Unit = { controlsModifier ->
                Column(
                    modifier = controlsModifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = state.personName,
                        onValueChange = viewModel::updatePersonName,
                        label = { Text(stringResource(R.string.name)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    ColorPickerGrid(
                        parts = state.parts,
                        onSelect = viewModel::selectColor,
                        onAddColor = { onAddColor(it.part.paletteId) },
                        columns = columns,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    ExportButtons(
                        onSave = ::onSaveClick,
                        onShare = ::onShareClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                    )
                }
            }

            // weight を効かせるため、親には必ず高さ・幅を与えておく。
            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    preview(Modifier.weight(1f).fillMaxHeight())
                    controls(Modifier.weight(1f).fillMaxHeight())
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    preview(Modifier.fillMaxWidth().weight(0.55f))
                    Spacer(Modifier.height(12.dp))
                    controls(Modifier.fillMaxWidth().weight(0.45f))
                }
            }
        }
    }
}

@Composable
private fun ExportButtons(
    onSave: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(onClick = onSave, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.save))
        }
        OutlinedButton(onClick = onShare, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.share))
        }
    }
}
