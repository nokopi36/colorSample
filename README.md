# O Color Design

義肢装具用カラーサンプル作成Androidアプリケーション

## 概要

O Color Designは、義肢装具（プロテーゼ・ブレース）のカラーサンプル画像を作成するためのAndroidアプリケーションです。様々な素材（革、プラスチック、糸、ボタンなど）の色を選択し、視覚的なサンプルを生成することができます。

## 機能

### 対応デバイス
- **Aブレース** - 下肢装具
- **ナイトブレース (NB)** - 夜間装着用装具
- **SLB** - 下肢装具
- **フットプレート (FTN)** - 足底装具
- **プレーリーくん (PL)** - 義足
- **ポーゴスティック (POGO)** - 義足

### カラーカスタマイズ
各デバイスに対して以下の素材の色をカスタマイズ可能：
- 革（レザー）
- プラスチック
- 糸
- ボタン
- スポンジ
- ベルト
- その他デバイス固有の部品

### 利用可能な色
25種類以上のカラーオプション：
- 基本色：赤、青、緑、黄色、オレンジ、ピンク、黒、白
- 特殊色：アクアマリン、ベージュローズ、ハチミツ、菊茶なし、焦げ茶、バニラなど

## システム要件

- **Android**: API Level 21 (Android 5.0) 以上
- **Target SDK**: 34
- **画面向き**: ポートレート固定
- **アーキテクチャ**: ARM、x86対応

## 技術仕様

### 開発環境
- **言語**: Kotlin
- **フレームワーク**: Android Data Binding
- **アーキテクチャ**: MVVM (Model-View-ViewModel)
- **ビルドツール**: Android Gradle Plugin 8.2.2
- **Java互換性**: Java 17

### 主要ライブラリ
```gradle
androidx.core:core-ktx:1.12.0
androidx.appcompat:appcompat:1.6.1
com.google.android.material:material:1.11.0
androidx.constraintlayout:constraintlayout:2.1.4
androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0
com.google.android.play:core:1.10.3
```

### プロジェクト構成

```
com.nokopi.colorsample/
├── activities/          # メインアクティビティ群
│   ├── MainActivity.kt
│   ├── ACustomColor.kt
│   ├── NBCustomColor.kt
│   ├── SLBCustomColor.kt
│   ├── FTNCustomColor.kt
│   ├── PLCustomColor.kt
│   └── POGOCustomColor.kt
├── ui/                  # ViewModelクラス
│   ├── AViewModel.kt
│   └── FTNViewModel.kt
├── utils/               # ユーティリティクラス
│   ├── BindingAdapters.kt
│   ├── ChangeColors.kt
│   ├── CustomColor.kt
│   └── KeyboardUtils.kt
└── view/                # カスタムUIコンポーネント
    ├── CustomSpinnerAdapter.kt
    └── Custom*Button.kt
```

## セットアップとビルド

### 前提条件
- Android Studio Arctic Fox 以降
- Kotlin プラグイン
- Android SDK 34

### ビルドコマンド

#### プロジェクトのビルド
```bash
./gradlew build
```

#### テスト実行
```bash
# 単体テスト
./gradlew test

# 機器連携テスト（接続されたデバイス/エミュレーター必須）
./gradlew connectedAndroidTest
```

#### リリースビルド作成
```bash
./gradlew assembleRelease
```

#### クリーンビルド
```bash
./gradlew clean
```

## アーキテクチャ

### MVVM パターン
- **Model**: カラーデータ管理（`CustomColor`, `ChangeColors`）
- **View**: Data Bindingを使用したレイアウト
- **ViewModel**: UIロジックとデータバインディング

### Data Binding
- レイアウトファイルとViewModelの自動バインディング
- カスタムバインディングアダプターによる複雑なUI更新
- LiveDataによるリアクティブUI更新

### カラーシステム
色の管理は以下の2つのクラスで集中管理：
- `CustomColor`: 全色のHEXカラーコード定義
- `ChangeColors`: 各素材のカラーマップと色適用メソッド

## 使用方法

1. アプリを起動し、メイン画面から希望するデバイスタイプを選択
2. 各部品のカラースピナーから希望する色を選択
3. リアルタイムでプレビューが更新される
4. 設定完了後、カラーサンプル画像を保存または共有

## バージョン情報

- **現在のバージョン**: 1.6 (Build 20)
- **対応言語**: 日本語
- **アプリ名**: O Color Design
- **パッケージ名**: com.nokopi.colorsample

## ライセンスとプライバシー

アプリ内でプライバシーポリシーを確認できます。Google Play Core ライブラリを使用してアプリ内アップデート機能を提供しています。

## 開発者向け情報

### カスタマイズ
- 新しい色の追加: `CustomColor.kt`でHEXコードを定義し、`ChangeColors.kt`でマップに追加
- 新デバイス追加: 既存パターンに従って新しいActivityとViewModelを作成
- UI調整: Data Bindingレイアウトファイルを編集

### デバッグ
- レイアウトインスペクターでData Binding状態を確認可能
- ViewModelのLiveDataによるデータフロー監視

---

このアプリケーションは義肢装具業界の専門的なニーズに対応するため、特定の用途に特化して設計されています。
