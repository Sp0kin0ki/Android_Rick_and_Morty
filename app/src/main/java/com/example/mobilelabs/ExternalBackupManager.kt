package com.example.mobilelabs

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupInfo(
    val exists: Boolean,
    val name: String = "",
    val location: String = "",
    val sizeBytes: Long = 0L,
    val modifiedAt: Long = 0L
)

object ExternalBackupManager {
    private const val FILE_NAME = "RickAndMorty_characters.txt"
    private const val RELATIVE_DIR = "Documents/RickAndMorty/"
    private const val PRIVATE_BACKUP = "rick_morty_external_backup.txt"

    private fun findPublicFile(context: Context): Uri? {
        val collection = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME}=? AND ${MediaStore.MediaColumns.RELATIVE_PATH}=?"
        context.contentResolver.query(collection, projection, selection, arrayOf(FILE_NAME, RELATIVE_DIR), null)?.use { cursor ->
            if (cursor.moveToFirst()) return Uri.withAppendedPath(collection, cursor.getLong(0).toString())
        }
        return null
    }

    fun info(context: Context): BackupInfo {
        val uri = findPublicFile(context) ?: return BackupInfo(false)
        return try {
            context.contentResolver.query(
                uri,
                arrayOf(MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATE_MODIFIED),
                null, null, null
            )?.use { cursor ->
                if (!cursor.moveToFirst()) return BackupInfo(false)
                BackupInfo(
                    exists = true,
                    name = cursor.getString(0) ?: FILE_NAME,
                    location = "Documents/RickAndMorty/$FILE_NAME",
                    sizeBytes = cursor.getLong(1),
                    modifiedAt = cursor.getLong(2) * 1000L
                )
            } ?: BackupInfo(false)
        } catch (_: Exception) { BackupInfo(false) }
    }

    fun create(context: Context): Result<BackupInfo> = runCatching {
        val lines = CharacterRepository.characters.joinToString("\n") {
            "Name: ${it.name}\nSpecies: ${it.species}\nStatus: ${it.status}\nLocation: ${it.location}\n"
        }
        require(lines.isNotBlank()) { "Сначала загрузите персонажей на экране данных." }
        val bytes = lines.toByteArray(Charsets.UTF_8)
        val resolver = context.contentResolver
        val existing = findPublicFile(context)
        val uri = existing ?: resolver.insert(
            MediaStore.Files.getContentUri("external"),
            ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, FILE_NAME)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, RELATIVE_DIR)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }
        ) ?: error("Не удалось создать файл в Documents.")
        resolver.openOutputStream(uri, "wt")?.use { it.write(bytes) }
            ?: error("Не удалось записать файл.")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.update(uri, ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }, null, null)
        }
        File(context.filesDir, PRIVATE_BACKUP).writeBytes(bytes)
        info(context).copy(exists = true)
    }

    fun deletePublicAndKeepPrivateCopy(context: Context): Result<Boolean> = runCatching {
        val uri = findPublicFile(context) ?: return@runCatching false
        // Скрытая резервная копия хранится во внутреннем хранилище приложения до удаления.
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: error("Не удалось прочитать файл перед удалением.")
        File(context.filesDir, PRIVATE_BACKUP).writeBytes(bytes)
        context.contentResolver.delete(uri, null, null) > 0
    }

    fun restore(context: Context): Result<Boolean> = runCatching {
        val privateFile = File(context.filesDir, PRIVATE_BACKUP)
        if (!privateFile.exists()) return@runCatching false
        val resolver = context.contentResolver
        val uri = findPublicFile(context) ?: resolver.insert(
            MediaStore.Files.getContentUri("external"),
            ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, FILE_NAME)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, RELATIVE_DIR)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }
        ) ?: error("Не удалось восстановить файл.")
        resolver.openOutputStream(uri, "wt")?.use { it.write(privateFile.readBytes()) }
            ?: error("Не удалось записать восстановленный файл.")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.update(uri, ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }, null, null)
        }
        true
    }

    fun hasPrivateBackup(context: Context): Boolean =
        File(context.filesDir, PRIVATE_BACKUP).exists()

    fun formatSize(bytes: Long): String = when {
        bytes < 1024 -> "$bytes Б"
        bytes < 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f КБ", bytes / 1024.0)
        else -> String.format(Locale.getDefault(), "%.1f МБ", bytes / (1024.0 * 1024.0))
    }

    fun formatDate(time: Long): String =
        if (time <= 0) "неизвестно" else SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(time))
}
