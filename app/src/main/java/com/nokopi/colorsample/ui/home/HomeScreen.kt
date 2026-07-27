package com.nokopi.colorsample.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.nokopi.colorsample.R
import com.nokopi.colorsample.data.model.Device
import com.nokopi.colorsample.data.model.DeviceId
import com.nokopi.colorsample.data.model.resolve
import com.nokopi.colorsample.ui.device.rememberPartPainter

/**
 * 装具を選ぶホーム画面。並び順は組み込み6種のあとにユーザーが作ったもの。
 *
 * ユーザーが作った装具は長押しで編集・削除できる。組み込みは変えられない。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    versionName: String,
    devices: List<Device>,
    hiddenDevices: List<Device>,
    onSelectDevice: (DeviceId) -> Unit,
    onAddDevice: () -> Unit,
    onEditDevice: (DeviceId) -> Unit,
    onDeleteDevice: (DeviceId) -> Unit,
    onHideDevice: (DeviceId) -> Unit,
    onUnhideDevice: (DeviceId) -> Unit,
    onManageColors: () -> Unit,
    onOpenSavedSchemes: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pendingDelete by remember { mutableStateOf<Device?>(null) }
    var showHidden by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    HomeOverflowMenu(
                        hiddenCount = hiddenDevices.size,
                        onManageColors = onManageColors,
                        onOpenSavedSchemes = onOpenSavedSchemes,
                        onShowHidden = { showHidden = true },
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        // フッターを bottomBar に置くと、Scaffold が FAB をその上に配置してくれる。
        // コンテンツ内に置いていたときは FAB が重なってバージョン表記を隠していた。
        bottomBar = {
            HomeFooter(versionName = versionName, onOpenPrivacyPolicy = onOpenPrivacyPolicy)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddDevice,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.add_device)) },
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

            devices.forEach { device ->
                DeviceCard(
                    device = device,
                    onClick = { onSelectDevice(device.id) },
                    onEdit = { onEditDevice(device.id) },
                    onDelete = { pendingDelete = device },
                    onHide = { onHideDevice(device.id) },
                )
            }
            // 全部非表示にすると一覧が空になる。戻し方に辿れるよう案内を出す。
            val emptyMessage = when {
                devices.isEmpty() -> R.string.no_visible_devices
                devices.none { !it.isBuiltIn } -> R.string.no_user_devices
                else -> null
            }
            emptyMessage?.let {
                Text(
                    text = stringResource(it),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // FAB はフッターより上・コンテンツの上に浮くので、
            // 最後のカードが隠れないぶんだけ空けておく。
            Spacer(modifier = Modifier.height(FAB_CLEARANCE))
        }
    }

    if (showHidden && hiddenDevices.isNotEmpty()) {
        HiddenDevicesDialog(
            hidden = hiddenDevices,
            onUnhide = onUnhideDevice,
            onDismiss = { showHidden = false },
        )
    }

    pendingDelete?.let { device ->
        val name = device.label.resolve()
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.delete_device_title, name)) },
            text = { Text(stringResource(R.string.delete_device_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDelete = null
                        onDeleteDevice(device.id)
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

/** FAB の高さぶんの余白。最後のカードが FAB の下に潜らないようにする。 */
private val FAB_CLEARANCE = 88.dp

/**
 * プライバシーポリシーとバージョン表記のフッター。
 *
 * `bottomBar` に渡す composable には Scaffold がインセットを当てないので、
 * ナビゲーションバーのぶんは自分で避ける。
 */
@Composable
private fun HomeFooter(
    versionName: String,
    onOpenPrivacyPolicy: () -> Unit,
) {
    Surface {
        Column(modifier = Modifier.navigationBarsPadding()) {
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onOpenPrivacyPolicy) {
                    Text(stringResource(R.string.privacyPolicy))
                }
                Text(
                    text = stringResource(R.string.version_format, versionName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun HomeOverflowMenu(
    hiddenCount: Int,
    onManageColors: () -> Unit,
    onOpenSavedSchemes: () -> Unit,
    onShowHidden: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.more_options),
        )
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.saved_schemes)) },
            onClick = {
                expanded = false
                onOpenSavedSchemes()
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.manage_colors)) },
            onClick = {
                expanded = false
                onManageColors()
            },
        )
        // 全部隠しても戻せるように、ここからは常に辿れるようにしておく。
        if (hiddenCount > 0) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.hidden_devices, hiddenCount)) },
                onClick = {
                    expanded = false
                    onShowHidden()
                },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DeviceCard(
    device: Device,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onHide: () -> Unit,
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
                .combinedClickable(
                    onClick = onClick,
                    // 組み込みも非表示にはできるので、長押しはどの装具でも受ける。
                    onLongClick = { menuExpanded = true },
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                painter = rememberPartPainter(device.thumbnail),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(72.dp),
            )
            Text(
                text = device.label.resolve(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )

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
                    // 非表示はどの装具にもできる。組み込みは定義を消せないのでこれが「消す」操作。
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.hide_device)) },
                        onClick = {
                            menuExpanded = false
                            onHide()
                        },
                    )
                    // 編集と削除はユーザーが作った装具だけ。
                    if (!device.isBuiltIn) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.edit_device)) },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delete_device)) },
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
}

/** ホームから外した装具の一覧。ここから1件ずつ戻す。 */
@Composable
private fun HiddenDevicesDialog(
    hidden: List<Device>,
    onUnhide: (DeviceId) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.hidden_devices_title)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                hidden.forEach { device ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Image(
                            painter = rememberPartPainter(device.thumbnail),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(40.dp),
                        )
                        Text(
                            text = device.label.resolve(),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(onClick = { onUnhide(device.id) }) {
                            Text(stringResource(R.string.restore_hidden_colors))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.ok)) }
        },
    )
}
