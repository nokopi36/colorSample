package com.nokopi.colorsample.ui.device

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nokopi.colorsample.R
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.ColorOption
import com.nokopi.colorsample.data.model.PartId
import com.nokopi.colorsample.data.model.resolve

/** 色の見本。白が背景に溶けないよう必ず輪郭線を引く。 */
@Composable
fun ColorSwatch(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
    )
}

/**
 * パーツ1つ分の色選択。ラベルは入力欄のラベルとして出すので、
 * 「カフバンド」のような長いパーツ名でも横幅を食い合わない。
 *
 * 一覧の末尾に「色を追加…」を置き、足りない色をその場で足しに行けるようにしている。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerField(
    label: String,
    options: List<ColorOption>,
    selected: ColorOption,
    onSelect: (ColorId) -> Unit,
    onAddColor: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selected.label.resolve(),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            leadingIcon = { ColorSwatch(selected.color) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            // ラベルと値は TextField 自身が読み上げるので、追加の semantics は付けない。
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label.resolve()) },
                    leadingIcon = { ColorSwatch(option.color, size = 24.dp) },
                    onClick = {
                        onSelect(option.id)
                        expanded = false
                    },
                )
            }

            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(stringResource(R.string.add_color)) },
                onClick = {
                    expanded = false
                    onAddColor()
                },
            )
        }
    }
}

/**
 * パーツ一覧。[columns] は呼び出し側が使える横幅から決める。
 */
@Composable
fun ColorPickerGrid(
    parts: List<PartSelection>,
    onSelect: (PartId, ColorId) -> Unit,
    onAddColor: (PartSelection) -> Unit,
    columns: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        parts.chunked(columns).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                row.forEach { selection ->
                    ColorPickerField(
                        label = selection.part.label.resolve(),
                        options = selection.palette.options,
                        selected = selection.selected,
                        onSelect = { onSelect(selection.part.id, it) },
                        onAddColor = { onAddColor(selection) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
