package com.example.mobilelabs

import android.content.Context
import com.example.mobilelabs.data.AppDatabase
import com.example.mobilelabs.data.CharacterEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class Character(
    val id: Int,
    val name: String,
    val species: String,
    val status: String,
    val location: String,
    val image: String = ""
)

object CharacterRepository {

    private fun dao(context: Context) = AppDatabase.get(context).characterDao()

    /** Реактивный поток персонажей — при изменении БД список обновляется сам. */
    fun observeCharacters(context: Context): Flow<List<Character>> =
        dao(context).observeAll().map { entities ->
            entities.map { it.toModel() }
        }

    suspend fun hasData(context: Context): Boolean = dao(context).count() > 0

    /** Одноразовый снимок таблицы (для экспорта в файл). */
    suspend fun snapshot(context: Context): List<Character> =
        dao(context).getAll().map { it.toModel() }

    /** «Обновить»: перезаписывает таблицу данными выбранной страницы API. */
    suspend fun refreshFromApi(context: Context, page: Int): Int {
        val response = RickAndMortyApi.service.getCharacters(page)
        val entities = response.results.orEmpty().map { it.toEntity() }
        val dao = dao(context)
        dao.clear()
        dao.upsertAll(entities)
        return response.info?.pages ?: 1
    }

    /** «Загрузить ещё»: дописывает следующую страницу API в таблицу. */
    suspend fun loadMoreFromApi(context: Context, page: Int): Int {
        val response = RickAndMortyApi.service.getCharacters(page)
        val entities = response.results.orEmpty().map { it.toEntity() }
        dao(context).upsertAll(entities)
        return response.info?.pages ?: 1
    }

    suspend fun clear(context: Context) = dao(context).clear()

    private fun ApiCharacter.toEntity() = CharacterEntity(
        id = id,
        name = name,
        species = species ?: "Unknown",
        status = status ?: "Unknown",
        location = location?.name ?: "Unknown",
        image = image ?: ""
    )

    private fun CharacterEntity.toModel() = Character(
        id = id,
        name = name,
        species = species,
        status = status,
        location = location,
        image = image
    )
}
