package com.nokopi.colorsample.ui.palette

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.nokopi.colorsample.R

/**
 * 色を作る・直すダイアログ。
 *
 * 指定の色をそのまま入れたい場合に備えて 16 進の入力欄を置き、感覚で決めたい場合に備えて
 * 色相・彩度・明度のスライダーも置いてある。どちらを動かしても他方に反映される。
 *
 * 面状のピッカーにしていないのは、装具の色見本という用途では「狙った値に合わせる」ほうが
 * 大事で、指でなぞる操作だと微調整がしづらいため。
 */
@Composable
fun ColorEditorDialog(
    title: String,
    initialName: String,
    initialColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (name: String, color: Color) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }

    val initialHsv = remember(initialColor) { initialColor.toHsv() }
    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var brightness by remember { mutableFloatStateOf(initialHsv[2]) }

    var hexText by remember { mutableStateOf(initialColor.toHexString()) }

    // 16 進で入力された色はそのまま保つ。HSV に落として組み立て直すと
    // 丸めで 1 ずれることがあり、指定値を入れたつもりの色が変わってしまう。
    // スライダーを動かした時点で null に戻し、以降は HSV から作る。
    var typedColor by remember { mutableStateOf<Color?>(initialColor) }

    val hexColor = parseHexColor(hexText)
    val color = typedColor ?: Color.hsv(hue, saturation, brightness)

    fun onHsvChanged() {
        typedColor = null
        hexText = Color.hsv(hue, saturation, brightness).toHexString()
    }

    fun onHexChanged(input: String) {
        hexText = input
        parseHexColor(input)?.let { parsed ->
            typedColor = parsed
            // スライダーの位置も追従させる。ここから続けてスライダーを触れば
            // この色を起点に調整できる。
            val hsv = parsed.toHsv()
            hue = hsv[0]
            saturation = hsv[1]
            brightness = hsv[2]
        }
    }

    val nameIsBlank = name.isBlank()
    val hexIsInvalid = hexColor == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.color_name)) },
                    singleLine = true,
                    isError = nameIsBlank,
                    supportingText = if (nameIsBlank) {
                        { Text(stringResource(R.string.color_name_required)) }
                    } else {
                        null
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .width(56.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(8.dp),
                            ),
                    )
                    OutlinedTextField(
                        value = hexText,
                        onValueChange = ::onHexChanged,
                        label = { Text(stringResource(R.string.hex_color)) },
                        singleLine = true,
                        isError = hexIsInvalid,
                        supportingText = {
                            Text(
                                if (hexIsInvalid) {
                                    stringResource(R.string.hex_color_invalid)
                                } else {
                                    stringResource(R.string.hex_color_hint)
                                },
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }

                LabeledSlider(stringResource(R.string.hue), hue, 0f..360f) {
                    hue = it
                    onHsvChanged()
                }
                LabeledSlider(stringResource(R.string.saturation), saturation, 0f..1f) {
                    saturation = it
                    onHsvChanged()
                }
                LabeledSlider(stringResource(R.string.brightness), brightness, 0f..1f) {
                    brightness = it
                    onHsvChanged()
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name.trim(), color) },
                enabled = !nameIsBlank && !hexIsInvalid,
            ) {
                Text(stringResource(R.string.save_color))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun Color.toHsv(): FloatArray =
    FloatArray(3).also { android.graphics.Color.colorToHSV(toArgb(), it) }
