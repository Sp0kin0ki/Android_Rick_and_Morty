package com.example.mobilelabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException

data class Character(
    val name: String,
    val species: String,
    val status: String,
    val location: String
)

@Composable
fun ContentScreen(
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backgroundColor = Color(0xFF101118)
    val greenColor = Color(0xFF97CE4C)
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentPage by remember { mutableIntStateOf(0) }
    var totalPages by remember { mutableIntStateOf(1) }
    var loadedFromCache by remember { mutableStateOf(false) }

    suspend fun loadPage(page: Int, append: Boolean) {
        isLoading = true
        errorMessage = null
        try {
            val response = RickAndMortyApi.service.getCharacters(page)
            val apiItems = response.results.orEmpty()
            if (apiItems.isEmpty()) {
                errorMessage = "Сервер вернул пустой список."
            } else {
                val mapped = apiItems.map { item ->
                    Character(
                        name = item.name,
                        species = item.species ?: "Unknown",
                        status = item.status ?: "Unknown",
                        location = item.location?.name ?: "Unknown"
                    )
                }
                if (append) CharacterRepository.appendToCache(context, mapped)
                else CharacterRepository.saveCache(context, mapped)
                currentPage = page
                totalPages = (response.info?.pages ?: 1).coerceAtLeast(1)
                loadedFromCache = false
            }
        } catch (exception: Exception) {
            errorMessage = when (exception) {
                is IOException -> "Нет подключения к интернету. Проверьте сеть и попробуйте ещё раз."
                is HttpException -> "Ошибка сервера: HTTP ${exception.code()}."
                else -> "Не удалось загрузить персонажей: ${exception.localizedMessage ?: "неизвестная ошибка"}"
            }
            if (page == 1 && CharacterRepository.loadCache(context)) {
                loadedFromCache = true
                currentPage = 1
                errorMessage = "Нет соединения. Показаны сохранённые данные."
            }
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        if (CharacterRepository.characters.isNotEmpty()) {
            loadPage(1, append = false)
        } else {
            loadPage(1, append = false)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(backgroundColor)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "RICK & MORTY",
                    color = greenColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Characters · REST API",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }

            IconButton(
                onClick = onSettingsClick
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = greenColor
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { scope.launch { loadPage(1, append = false) } },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = greenColor)
            ) {
                Text("Обновить", color = Color(0xFF101118))
            }
            Text(
                text = if (currentPage > 0) "Страница $currentPage из $totalPages" else "Загрузка данных",
                color = Color.LightGray,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        if (loadedFromCache) {
            Text(
                text = "Офлайн-режим: данные взяты из локального кэша",
                color = greenColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                color = if (loadedFromCache) greenColor else Color(0xFFFF8A80),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (isLoading && CharacterRepository.characters.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = greenColor)
                Text("Загружаем персонажей…", color = Color.LightGray, modifier = Modifier.padding(top = 12.dp))
            }
        } else if (CharacterRepository.characters.isEmpty() && !isLoading) {
            Text(
                text = "Персонажи пока не загружены. Проверьте интернет и нажмите «Обновить».",
                color = Color.LightGray,
                modifier = Modifier.padding(20.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(CharacterRepository.characters) { character ->
                    CharacterCard(character = character)
                }
                item {
                    Button(
                        onClick = {
                            if (!isLoading && currentPage < totalPages) {
                                scope.launch { loadPage(currentPage + 1, append = true) }
                            }
                        },
                        enabled = !isLoading && currentPage < totalPages,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = greenColor)
                    ) {
                        Text(
                            text = if (isLoading) "Загрузка…" else if (currentPage < totalPages) "Загрузить следующую страницу" else "Все страницы загружены",
                            color = Color(0xFF101118)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CharacterCard(
    character: Character
) {

    val greenColor = Color(0xFF97CE4C)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B1D26)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(character.name, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Species: ${character.species}", color = Color.LightGray, fontSize = 14.sp)
            Text(
                "Status: ${character.status}",
                color = if (character.status.equals("Alive", ignoreCase = true)) greenColor else Color.LightGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text("Location: ${character.location}", color = Color.LightGray, fontSize = 14.sp)
        }
    }
}