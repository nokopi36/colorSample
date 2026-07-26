package com.nokopi.colorsample.ui.device

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nokopi.colorsample.R
import com.nokopi.colorsample.data.model.PartImage
import com.nokopi.colorsample.ui.theme.PreviewCanvas
import com.nokopi.colorsample.ui.theme.PreviewCanvasContent
import com.nokopi.colorsample.ui.theme.PreviewCanvasOutline

/**
 * 配色プレビュー。パーツ画像を順に重ね、選ばれた色で tint する。
 *
 * Compose 移行前は `Drawable.setTint()` した Drawable を ImageView に流し込んでいたが、
 * `ContextCompat.getDrawable()` の戻り値は ConstantState を共有するため
 * `mutate()` を挟まないと同じ画像を使う別の描画にも色が漏れていた。
 * Compose では描画時の [ColorFilter] なので元の画像には触れない。
 * tint の見え方は `setTint` の既定と同じ [androidx.compose.ui.graphics.BlendMode.SrcIn]。
 *
 * @param graphicsLayer 渡すと描画内容をここに記録し、保存・共有用のビットマップを取り出せる。
 */
@Composable
fun ColorPreview(
    deviceLabel: String,
    parts: List<PartSelection>,
    overlay: PartImage?,
    personName: String,
    modifier: Modifier = Modifier,
    graphicsLayer: GraphicsLayer? = null,
) {
    val recording = if (graphicsLayer == null) {
        Modifier
    } else {
        Modifier.drawWithContent {
            graphicsLayer.record { this@drawWithContent.drawContent() }
            drawLayer(graphicsLayer)
        }
    }

    Column(
        // 記録はチェーンの先頭に置く。ここから後ろ (下地・枠・画像) が drawContent() に含まれるので、
        // 書き出した PNG の背景が透明にならない。
        modifier = modifier
            .then(recording)
            .clip(RoundedCornerShape(12.dp))
            .background(PreviewCanvas)
            .border(1.dp, PreviewCanvasOutline, RoundedCornerShape(12.dp)),
    ) {
        if (personName.isNotBlank()) {
            Text(
                text = personName,
                color = PreviewCanvasContent,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }

        // 氏名の行を差し引いた残りを画像に充てる。
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp),
        ) {
            val description = stringResource(R.string.preview_description, deviceLabel)

            parts.forEachIndexed { index, selection ->
                Image(
                    painter = rememberPartPainter(selection.part.image),
                    contentDescription = if (index == 0) description else null,
                    colorFilter = ColorFilter.tint(selection.selected.color),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // 輪郭など色を変えない最前面のレイヤー
            overlay?.let {
                Image(
                    painter = rememberPartPainter(it),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
