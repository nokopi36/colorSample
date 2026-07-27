# CLAUDE.md

Guidance for Claude Code when working in this repository. What the app *is* and how to run it lives in
`README.md`; this file is for **what you must know before editing** — invariants, traps, and why things
are the way they are.

## Build

```bash
./gradlew assembleDebug      # Build
./gradlew testDebugUnitTest  # Unit tests
./gradlew lintDebug          # Lint (fails the build on errors)
./gradlew assembleRelease
./gradlew clean
./gradlew connectedAndroidTest   # Instrumented tests (needs a device/emulator)
```

Kotlin + Jetpack Compose + Material3, Navigation 3, single Activity. No XML layouts, no DataBinding.
**Versions live only in `gradle/libs.versions.toml`** — don't duplicate them into docs or build files.

Constraints to know before bumping anything:
- Navigation 3 requires **AGP 8.9.1+**. `lifecycle` **2.11+** would require AGP 9.1+, so lifecycle is
  pinned to the 2.10 line to stay on AGP 8.x.
- Nav 3 pulls Compose 1.10+, which forces **minSdk 23**. Dropping below that is not possible while Nav 3
  is in use.
- `material-icons-core` must be declared explicitly — material3 1.4 dropped it as a transitive dependency.
- `app-update-ktx` drags in `fragment:1.1.0`, which breaks the ActivityResult APIs. A `constraints` block
  in `app/build.gradle.kts` bumps it; removing that reintroduces a lint error.

---

## The one idea that shapes the whole app

Every device screen is **the same screen**: stack N part images, tint each with a colour chosen from that
part's palette, draw them in list order. The only differences between devices are the part count, the
images, the labels, and which palette each part uses.

That difference lives entirely in **data**. There is exactly one screen composable
(`ui/device/DeviceColorScreen.kt`) and one ViewModel (`ui/device/DeviceColorViewModel.kt`) for all devices.

**To add or change a device, edit the catalog — never add a screen or a ViewModel.**

## Catalog: built-in + user-defined, merged

```
data/BuiltInCatalog.kt    APK に同梱された色と装具。ID は一度出したら変えない
data/store/               保存形式 (@Serializable DTO)。ユーザーが足したぶんだけ
data/CatalogMerger.kt     両者を1つの Catalog にまとめる純粋関数
data/CatalogRepository.kt 画面が見る入り口。Flow<Catalog> と更新API
data/model/               実行時モデル (Catalog / Device / PartSpec / Palette / ColorOption)
```

### 実行時モデルと保存形式を分けている理由

`DisplayText.Res` と `PartImage.Bundled` は**リソースIDを持つ**。リソースIDはビルドごとに変わりうるので
**保存してはいけない**。`data/store` 側は常に文字列と数値だけで完結させ、`data/model` 側だけが
リソースIDを持つ。この境界を壊すと、次のリリースで保存データの参照が切れる。

### ID の規約

- 色・装具・パーツ: 組み込みは `builtin:`、ユーザー定義は `user:` 接頭辞（`ColorId.isBuiltIn`）
- **パレットだけ例外** — 組み込みは `leather` のように接頭辞を持たない。既存の保存データ（装具の
  パーツ）がこの ID を参照しているため変えられない。判定は `PaletteId.isUserDefined`（**色とは逆向き**）
- `BuiltInCatalog` の ID は**一度出したものを変えない**。名前や色を直すのは構わない

### 守るべき不変条件: パレットは必ず1色以上

`Palette.optionOrFirst` が `options.first()` を呼ぶので、空パレットは落ちる。3か所で守っている。

1. **作成時** — グループ追加は名前と最初の色を続けて入力させる。色0件では作れない
2. **削除時** — そのグループで最後の1色は非表示・削除できない（UI で無効化＋リポジトリでも拒否）
3. **読み込み時** — 全部消えてしまう非表示指定は無視して自己修復する（`CatalogMerger.splitHidden`）

`Palette` の `init` に `require` があるので、崩れたら原因のわかる形で落ちる。

### 組み込みを「消す」= 非表示

組み込みの色と装具はコード上の定義を消せないので、保存側の非表示リストで表す。定義は残るため
アプリ更新で壊れず、いつでも戻せる。

**色の非表示は (グループ, 色) の組。** 組み込みの色は複数グループで共有されている（黒は9グループ、
白は8）ので、色だけを指定すると関係ないグループからも消える。

非表示の件数は保存側の指定数ではなく `Palette.hiddenOptions`（実際に効いたぶん）から出す。
上記の自己修復が働くと保存側と実態がずれ、「戻す」の表示が嘘になる。

装具の非表示は `Catalog.hiddenDevices`。色と違い全部隠しても落ちない（ホームが空になるだけ）ので
不変条件は敷いていない。代わりに戻す入口を、一覧が空でも辿れるオーバーフローメニューに置いている。

### 保存した配色

画像に書き出しても色名が残らないので、パーツごとの色を ID のまま保存して開き直せるようにしている
（`StoredScheme` / `SavedScheme`）。参照する ID はすべて文字列なので、上記の「保存側はリソースIDを
持たない」境界はそのまま守られる。

解決は `CatalogMerger` が行い、**装具を引けない配色は落とす**。削除された装具だけでなく、
**非表示にした装具の配色も一覧から消える**。残すと開いた先で `Catalog.device` が引けず、
`NotFound` を経てホームへ弾かれるため。保存側の定義は残しているので装具を戻せば配色も戻る。
装具の削除時は `deleteDevice` が配色も一緒に消す（そちらは戻ってこないので）。

色の参照が切れている場合と、装具にあとからレイヤーが増えて保存側に無いパーツは、配色画面と同じく
`optionOrFirst` でパレット先頭に落とす。

### 色を消すと配色の見え方が変わる

色を削除・非表示にしても `StoredScheme` は書き換わらない。壊れるのは解決だけで、色 ID はそのまま
残り `optionOrFirst` が先頭に落とす。**戻せるかどうかで扱いを分けている。**

- **非表示** — 「非表示にした色 N件 → 戻す」からいつでも復活でき、同じ `ColorId` なので配色の色も
  元どおり。スナックバーだけで確認は出さない
- **削除** — スナックバーの「元に戻す」を逃すと戻せない（作り直すと新しい UUID になり relink
  しない）。`Catalog.schemesUsing` で**使っている配色があるときだけ**確認ダイアログを出す

グループ削除と違って**拒否はしない**。色は正当に引退させるものなので、何が変わるかを見せて進ませる。

`schemesUsing` は色だけでなく**グループも合わせて**見る。組み込みの色は複数グループで共有されて
いるので、色だけで引くと革の黒を消すときにボタンの黒を使う配色まで巻き込む。指定を省いたパーツが
落ちた先の色も「使っている」に入る（実際その色で表示されているため）。

### レイヤーは「色を変えない」を持てる

`PartSpec.paletteId` が **null なら色を変えないレイヤー**（線画・固定色の金具・影など）。
何枚でも持てて、色を変える層のあいだに挟める。`Device.parts` の並びがそのまま描画順（先頭が最背面）。

以前は「最前面の線画」を装具に1枚だけ持てる特別枠 (`Device.overlay`) にしていたが、複数持てず、
途中に挟めず、常に最前面に描かれるため一覧の並び順と描画順がずれていた。この形に戻さないこと。

## 描画

tint は描画時の `ColorFilter.tint(...)`（既定 `BlendMode.SrcIn`）。`Drawable` は一切変更しない。
Compose 移行前は `Drawable.setTint()` を使っていたが、`ContextCompat.getDrawable()` の戻り値は
ConstantState を共有するため、`mutate()` なしでは同じ画像を使う別の描画にも色が漏れていた。

色見本は Compose の円を描く（drawable リソースではない）。

`ColorPreview` は `graphicsLayer` を受け取って描画内容を記録し、保存・共有用のビットマップを出す。
**記録の Modifier はチェーンの先頭に置く。** 後ろに置くと下地と枠が `drawContent()` に含まれず、
書き出した PNG の背景が透明になる。

プレビューの下地はライト/ダークどちらでも固定の明色。色を見比べるためなので、テーマ依存にしないこと。

## 状態

`DeviceColorViewModel` は選択を **`Map<PartId, ColorId>`** で持ち、JSON 文字列にして
`SavedStateHandle` に保存する（Bundle に Map は置けない）。index で持つと、ユーザーが色を1つ
追加しただけで既存の選択がずれる。参照先の色が消えていたらパレット先頭に落とす。

ViewModel は `Context` を持たない（ラベルは Composable 側の `stringResource` で解決）。素の JUnit で
テストできる状態を保つこと。配色の保存も、リポジトリごと渡さず `suspend (SchemeSave) -> SchemeId` の
関数だけを受け取っているのはこのため。

**`SavedStateHandle` の既定値は `null`。** `"{}"` にすると「保存した配色から開いた直後」と
「ユーザーが明示的に空にした」が区別できず、配色を開いた瞬間に中身が消える。未操作なら配色の内容、
一度でも触ったら handle が勝つ、という優先順位で解決している。

これに紐づく罠がもう1つ。色を1つ選ぶときの土台は **handle ではなく表示中の状態**から作ること
（`effectiveSelections`）。未操作の handle は空なので、そこに差分を積むと変えていないパーツが
全部パレット先頭に戻る。`DeviceColorViewModelTest` が番人。

装具は**ルート引数ではない** — Navigation 3 に引数はない。`DeviceKey` が `deviceId` を持ち、
`ColorSampleNavDisplay` がコンストラクタへ渡す。`rememberViewModelStoreNavEntryDecorator()` が
ViewModel をエントリにスコープし、`createSavedStateHandle()` が必要とする `SavedStateRegistryOwner`
を供給する。**このデコレータを外すと状態復元が壊れる。**

表示中の装具が削除・非表示になった場合は `DeviceColorUiState.NotFound` を経てホームへ戻る。

## 装具エディタと画像の取り込み

編集は **staging** で行う。取り込んだ画像は `filesDir/devices/.staging/` に置き、保存でようやく
`devices/<id>/` へ移す。既存装具の編集でも最初に画像を staging へ写すので、途中でやめても元の装具は
無傷。エディタ起動時に staging を掃除するので中断の残骸も溜まらない。

取り込み時にファイルへ落としてしまうのは、`OpenMultipleDocuments` が返す URI の読み取り権限が
編集終了まで生きている保証がないため。

寸法とアルファの判定は **PNG ヘッダを自分で読む**（`ImageImport.readPngInfo`）。`BitmapFactory` の
`inJustDecodeBounds` は仕様上 `decodeStream` が null を返すため、そこに頼ると取りこぼす。自分で読めば
Android に依存しない純粋関数になりテストで固定できるし、カラータイプ 4/6 と `tRNS` を見るので
インデックスカラー + tRNS で書き出された PNG も通せる。

長辺 1440px を超える画像はダウンサンプルする。端末依存の OOM を避けるため。

## リソース

- パーツ画像は **`res/drawable-nodpi/`**。修飾子なしの `drawable/` は mdpi 扱いになり、3x 端末では
  1280×1280 が 3840×3840 に拡大読み込みされる（11層で理論上 650MB）。`nodpi` に置かないと OOM する
- `<device><n>.png` の最大番号が線画、`a.png` などがホームのサムネイル
- `res/values/` は文字列とランチャーアイコン色、起動時の下地用の最小 `Theme.ColorSample` だけ。
  実際のテーマは `ui/theme/`

## テスト

組み込み定義を固定するテストは、Compose 移行前のソースから**独立に書き起こした**期待値で押さえている。
`BuiltInCatalog` や `Palette` を変えるときは、意図した変更であることを確認してから期待値を直すこと。

| テスト | 守っているもの |
|---|---|
| `BuiltInCatalogTest` | 組み込み6装具のパーツ定義と全パレットの色・並び |
| `CatalogMergerTest` | ユーザー色/装具のマージ、並び順、解決できない参照の扱い |
| `PaletteMergeTest` | 非表示のグループ単位性、自己修復、改名、グループ追加 |
| `HiddenDeviceTest` | 装具の非表示、全部隠しても壊れないこと |
| `SavedSchemeMergeTest` | 保存した配色の解決、装具や色の参照が切れたときの扱い |
| `ImageImportTest` / `PngInfoTest` | 寸法・アルファ判定、間引き率、縮小後の寸法 |
| `DeviceColorViewModelTest` | 色の追加で選択がずれないこと、削除された色のフォールバック、配色から開いたときの初期値と上書き保存 |
| `DeviceEditorStateTest` | レイヤーの並べ替え、色を変えない層の切り替え、保存可否 |
| `HexColorTest` | 16進の解析と整形の往復 |
| `ManageColorsKeyTest` | LazyColumn のキーが全体で一意 |

## 過去に踏んだ罠（同じ形で再発しやすいもの）

- **LazyColumn の重複キー** — 色の管理画面は全グループを1つの LazyColumn に並べる。組み込みの色は
  複数グループに同じ `ColorId` で出るので、キーには必ずグループを混ぜる（`colorItemKey`）。
  `ManageColorsKeyTest` が番人
- **LazyColumn のスクロール位置の復元** — 末尾に固定の項目を置くと、データ到着前は項目がそれ1件だけに
  なり、LazyColumn がそのキーを先頭の基準として覚える。あとから前に挿入されると最下部まで動く。
  追加ボタンなどはリストの項目にせず FAB に置く
- **FAB とフッターの重なり** — フッターは `Scaffold` の `bottomBar` に置く。そうすれば FAB がその上に
  配置される。`bottomBar` には Scaffold がインセットを当てないので `navigationBarsPadding()` は自分で
- **真偽値を渡すトグル** — 「変えたあとの値」を呼び出し側で組み立てると、反転を間違えても型で
  気づけない（実際にチップが無反応になった）。トグルは引数を持たせず、反転をロジック側に閉じ込める

## その他

- アプリ内アップデートは `AppUpdateManager` + `ActivityResultContracts.StartIntentSenderForResult`
- 保存は API 29+ が `MediaStore`、28以下は `WRITE_EXTERNAL_STORAGE` + public Pictures。
  共有は `cacheDir/OColorDesign` を root にした `FileProvider`
- 日本語のみ。組み込みの色名・パーツ名は文字列リソース（`DisplayText.Res`）、ユーザーが入れた名前は
  そのままの文字列（`DisplayText.Literal`）
