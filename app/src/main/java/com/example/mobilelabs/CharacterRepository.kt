package com.example.mobilelabs

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.edit

object CharacterRepository {
    private const val PREFS_NAME = "rick_morty_preferences"
    private const val BACKUP_KEY = "characters_backup"

    val characters = mutableStateListOf(
        Character("Rick Sanchez", "Human", "Alive", "Earth (C-137)"),
        Character("Morty Smith", "Human", "Alive", "Earth (C-137)"),
        Character("Summer Smith", "Human", "Alive", "Earth (C-137)"),
        Character("Beth Smith", "Human", "Alive", "Earth (C-137)"),
        Character("Jerry Smith", "Human", "Alive", "Earth (C-137)"),
        Character("Mr. Meeseeks", "Humanoid", "Alive", "Earth (Replacement Dimension)"),
        Character("Birdperson", "Alien", "Alive", "Bird World"),
        Character("Squanchy", "Alien", "Alive", "Unknown"),
        Character("Evil Morty", "Human", "Alive", "Unknown"),
        Character("Pickle Rick", "Human", "Alive", "Earth (C-137)")
    )

    fun hasBackup(context: Context): Boolean =
        preferences(context).contains(BACKUP_KEY)

    fun createBackup(context: Context) {
        val serialized = characters.joinToString("\n") { character ->
            listOf(character.name, character.species, character.status, character.location)
                .joinToString("\t")
        }
        preferences(context).edit { putString(BACKUP_KEY, serialized) }
    }

    fun restoreBackup(context: Context): Boolean {
        val serialized = preferences(context).getString(BACKUP_KEY, null) ?: return false
        val restored = serialized.lineSequence()
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                val fields = line.split('\t')
                if (fields.size == 4) {
                    Character(fields[0], fields[1], fields[2], fields[3])
                } else null
            }
            .toList()

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
        restoreBackup(context)
    }

    private fun preferences(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
