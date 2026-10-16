package com.example.mobilelabs

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.edit

object CharacterRepository {
    private const val PREFS_NAME = "rick_morty_preferences"
    private const val BACKUP_KEY = "characters_backup"
    private const val CACHE_KEY = "characters_cache"

    val characters = mutableStateListOf<Character>()

    fun hasBackup(context: Context): Boolean =
        preferences(context).contains(BACKUP_KEY)

    fun createBackup(context: Context) {
        preferences(context).edit { putString(BACKUP_KEY, serialize(characters)) }
    }

    fun restoreBackup(context: Context): Boolean {
        val serialized = preferences(context).getString(BACKUP_KEY, null) ?: return false
        val restored = deserialize(serialized)
        if (restored.isEmpty()) return false
        characters.clear()
        characters.addAll(restored)
        return true
    }

    fun deleteBackup(context: Context): Boolean {
        val existed = hasBackup(context)
        preferences(context).edit { remove(BACKUP_KEY) }
        return existed
    }

    fun loadBackupIfAvailable(context: Context) {
        if (characters.isEmpty()) loadCache(context)
        if (characters.isEmpty()) restoreBackup(context)
    }

    fun saveCache(context: Context, items: List<Character>) {
        preferences(context).edit { putString(CACHE_KEY, serialize(items)) }
        characters.clear()
        characters.addAll(items)
    }

    fun appendToCache(context: Context, items: List<Character>) {
        val combined = (characters.toList() + items).distinctBy { it.name }
        saveCache(context, combined)
    }

    fun loadCache(context: Context): Boolean {
        val serialized = preferences(context).getString(CACHE_KEY, null) ?: return false
        val cached = deserialize(serialized)
        if (cached.isEmpty()) return false
        characters.clear()
        characters.addAll(cached)
        return true
    }

    private fun serialize(items: List<Character>): String =
        items.joinToString("\\n") { character ->
            listOf(character.name, character.species, character.status, character.location)
                .joinToString("\\t") { it.replace("\\t", " ").replace("\\n", " ") }
        }

    private fun deserialize(serialized: String): List<Character> =
        serialized.lineSequence()
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                val fields = line.split('\t')
                if (fields.size == 4) Character(fields[0], fields[1], fields[2], fields[3]) else null
            }
            .toList()

    private fun preferences(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
