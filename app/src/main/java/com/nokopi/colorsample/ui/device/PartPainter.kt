package com.nokopi.colorsample.ui.device

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.nokopi.colorsample.data.DeviceFiles
import com.nokopi.colorsample.data.model.PartImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [PartImage] を描ける形にする。
 *
 * 同梱画像はそのまま `painterResource`、ユーザーが取り込んだ画像は `filesDir` から読む。
 * 後者はデコードが重い（1000px 級 × 枚数）ので IO に逃がし、読み終わるまでは透明を返す。
 */
@Composable
fun rememberPartPainter(image: PartImage): Painter = when (image) {
    is PartImage.Bundled -> painterResource(image.res)
    is PartImage.Stored -> rememberStoredPainter(image)
}

private val Transparent = ColorPainter(Color.Transparent)

@Composable
private fun rememberStoredPainter(image: PartImage.Stored): Painter {
    val context = LocalContext.current
    val painter by produceState<Painter>(initialValue = Transparent, image.relativePath) {
        value = withContext(Dispatchers.IO) {
            val file = DeviceFiles.resolve(context, image.relativePath)
            val bitmap = runCatching { BitmapFactory.decodeFile(file.absolutePath) }.getOrNull()
            bitmap?.let { BitmapPainter(it.asImageBitmap()) } ?: Transparent
        }
    }
    return painter
}
