import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

/**
 * リリース署名の設定。
 *
 * リポジトリに鍵やパスワードを置かないため、プロジェクト直下の `keystore.properties`
 * （gitignore 済み）から読む。書き方は `keystore.properties.template` を参照。
 *
 * このファイルが無い環境ではあえて署名を付けず、未署名のまま release ビルドを通す。
 * クローン直後や CI でも `assembleRelease` / `bundleRelease` が動くようにするため。
 * ストアへ上げるものは必ず署名が必要なので、下の `hasReleaseSigning` で判別できる。
 */
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties: Properties? = keystorePropertiesFile.takeIf { it.exists() }?.let { file ->
    Properties().apply { file.inputStream().use(::load) }
}
val hasReleaseSigning = keystoreProperties != null

/**
 * `storeFile` だけは生のテキストから読む。
 *
 * `Properties` は `\` をエスケープとして解釈するので、Windows のパスをそのまま貼った
 * `C:\releaseKeystore\release-key.jks` は `\r` が改行文字に化けて壊れる。
 * パスは貼り付けで入れるものなので、こちら側で受けたほうが事故が少ない。
 *
 * パスワードは特殊文字のエスケープが効いたほうがよいので [Properties] 経由のままにしている。
 */
fun readRawStoreFile(file: File): String? = file.readLines()
    .firstOrNull { it.trimStart().startsWith("storeFile=") }
    ?.substringAfter('=')
    ?.trim()
    ?.takeIf { it.isNotEmpty() }
    // File は区切りが / でも解決できるので、貼り付けたままの \ を寄せておく。
    ?.replace('\\', '/')

android {
    namespace = "com.nokopi.colorsample"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.nokopi.colorsample"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (keystoreProperties != null) {
            create("release") {
                fun required(key: String): String = requireNotNull(
                    keystoreProperties.getProperty(key)?.takeIf { it.isNotBlank() },
                ) { "keystore.properties に $key がありません" }

                val path = requireNotNull(readRawStoreFile(keystorePropertiesFile)) {
                    "keystore.properties に storeFile がありません"
                }
                storeFile = rootProject.file(path).also {
                    // ここで気づかないと、署名タスクまで進んでから分かりにくい形で失敗する。
                    require(it.isFile) { "keystore が見つかりません: ${it.absolutePath}" }
                }
                storePassword = required("storePassword")
                keyAlias = required("keyAlias")
                keyPassword = required("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            // keystore.properties が無ければ null。その場合は未署名で出る。
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        // ホーム画面に出すバージョン表記を BuildConfig.VERSION_NAME から取るため。
        // (gradle.properties でプロジェクト既定は off にしてある)
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)

    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.play.app.update.ktx)

    constraints {
        // Fragment は直接使っていないが、app-update-ktx が fragment 1.1.0 を引き込む。
        // 1.3.0 未満の FragmentActivity は ActivityResult API を壊すため引き上げておく。
        implementation(libs.androidx.fragment) {
            because("app-update-ktx が引き込む fragment 1.1.0 は ActivityResult API と組み合わせられない")
        }
    }

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
