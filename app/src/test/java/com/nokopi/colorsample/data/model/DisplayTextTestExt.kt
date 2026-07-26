package com.nokopi.colorsample.data.model

/**
 * テストから表示名を読むためのヘルパ。
 *
 * 本番の [resolve] は `stringResource` を使う @Composable なので素の JUnit から呼べない。
 * ユーザーが入れた名前は [DisplayText.Literal] なのでそのまま読めるが、組み込みの
 * [DisplayText.Res] は文字列リソースの解決が必要になるため、ID を添えた形で返して
 * 「組み込みのままか / 付け替わったか」を見分けられるようにしている。
 */
fun DisplayText.resolveForTest(): String = when (this) {
    is DisplayText.Literal -> value
    is DisplayText.Res -> "res:$id"
}
