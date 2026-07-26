package com.nokopi.colorsample.data.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * 画面に出す名前。
 *
 * 組み込みの色名・パーツ名は文字列リソースのままにしておきたいが、ユーザーが付ける名前は
 * ただの文字列になる。両方を1つの型で扱うためのもの。
 *
 * これは実行時のモデルであって保存形式ではない。リソースIDはビルドごとに変わりうるので、
 * [Res] を永続化してはいけない。保存するのは `data/store` 側の DTO（常に文字列）。
 */
sealed interface DisplayText {

    /** 組み込み定義用。翻訳が効く。 */
    @JvmInline
    value class Res(@StringRes val id: Int) : DisplayText

    /** ユーザーが入力した名前。 */
    @JvmInline
    value class Literal(val value: String) : DisplayText
}

@Composable
fun DisplayText.resolve(): String = when (this) {
    is DisplayText.Res -> stringResource(id)
    is DisplayText.Literal -> value
}
