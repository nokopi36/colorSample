package com.nokopi.colorsample.data.store

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * ユーザーが追加した色・装具の保存。
 *
 * 件数がたかだか数十なので Room は使わず、JSON 1ドキュメントを DataStore で読み書きする。
 * DataStore が書き込みの直列化とアトミックな置き換えを引き受けてくれる。
 */
class CatalogStore(private val dataStore: DataStore<StoredCatalog>) {

    val catalog: Flow<StoredCatalog> = dataStore.data
        // 読めない場合に画面が落ちるより、組み込みだけで動くほうがまし。
        .catch { cause ->
            if (cause is IOException) emit(StoredCatalog.EMPTY) else throw cause
        }

    suspend fun update(transform: (StoredCatalog) -> StoredCatalog) {
        dataStore.updateData(transform)
    }

    companion object {
        private const val FILE_NAME = "catalog.json"

        fun create(context: Context): CatalogStore = CatalogStore(
            DataStoreFactory.create(serializer = CatalogSerializer) {
                context.dataStoreFile(FILE_NAME)
            },
        )
    }
}

private object CatalogSerializer : Serializer<StoredCatalog> {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override val defaultValue: StoredCatalog = StoredCatalog.EMPTY

    override suspend fun readFrom(input: InputStream): StoredCatalog =
        try {
            json.decodeFromString(
                StoredCatalog.serializer(),
                input.readBytes().decodeToString(),
            )
        } catch (e: SerializationException) {
            throw CorruptionException("保存されたカタログを読み込めませんでした", e)
        }

    override suspend fun writeTo(t: StoredCatalog, output: OutputStream) {
        output.write(json.encodeToString(StoredCatalog.serializer(), t).encodeToByteArray())
    }
}
