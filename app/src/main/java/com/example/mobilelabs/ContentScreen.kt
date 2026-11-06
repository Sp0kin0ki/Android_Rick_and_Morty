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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException

@Composable
fun ContentScreen(
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backgroundColor = Color(0xFF101118)
    val greenColor = Color(0xFF97CE4C)

    // Реактивный список: Room сам эмитит обновления при изменении таблицы.
    val characters by CharacterRepository.observeCharacters(context)
        .collectAsState(initial = emptyList())

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentPage by remember { mutableIntStateOf(0) }
    var totalPages by remember { mutableIntStateOf(1) }
    var offlineMode by remember { mutableStateOf(false) }
    var pageInput by remember { mutableStateOf("") }

    suspend fun fetch(page: Int, append: Boolean) {
        isLoading = true
        errorMessage = null
        try {
            val pages = if (append) {
                CharacterRepository.loadMoreFromApi(context, page)
            } else {
                CharacterRepository.refreshFromApi(context, page)
            }
            currentPage = page
            totalPages = pages.coerceAtLeast(1)
            offlineMode = false
        } catch (exception: Exception) {
            errorMessage = when (exception) {
                is IOException -> "Нет подключения к интернету. Показаны данные из локальной базы."
                is HttpException -> "Ошибка сервера: HTTP ${exception.code()}."
                else -> "Не удалось загрузить персонажей: ${exception.localizedMessage ?: "неизвестная ошибка"}"
            }
            if (CharacterRepository.hasData(context)) {
                offlineMode = true
            }
        } finally {
            isLoading = false
        }
    }

    // «Холодный старт»: если таблица пуста — грузим из сети, иначе показываем данные Room.
    LaunchedEffect(Unit) {
        if (!CharacterRepository.hasData(context)) {
            fetch(1, append = false)
        } else {
            offlineMode = true
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
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
                    text = "Characters · REST API + Room",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = greenColor
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { scope.launch { fetch(1, append = false) } },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = greenColor)
            ) {
                Text("Обновить", color = Color(0xFF101118))
            }
            Text(
                text = if (currentPage > 0) "Страница $currentPage из $totalPages" else "Данные из Room",
                color = Color.LightGray,
                modifier = Modifier.weight(1f)
            )
        }

        // «Загрузить ещё»: пользователь может указать другой порядковый номер страницы.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = pageInput,
                onValueChange = { input -> pageInput = input.filter { it.isDigit() }.take(3) },
                modifier = Modifier.width(110.dp),
                singleLine = true,
                label = { Text("Стр.") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = greenColor,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedLabelColor = greenColor,
                    unfocusedLabelColor = Color.LightGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = greenColor
                )
            )
            Button(
                onClick = {
                    val page = pageInput.toIntOrNull() ?: (currentPage + 1)
                    if (page in 1..totalPages) {
                        scope.launch { fetch(page, append = true) }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = greenColor)
            ) {
                Text("Загрузить ещё", color = Color(0xFF101118))
            }
        }

        if (offlineMode) {
            Text(
                text = "Офлайн-режим: данные взяты из локальной базы Room",
                color = greenColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                color = if (offlineMode) greenColor else Color(0xFFFF8A80),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (isLoading && characters.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = greenColor)
                Text(
                    "Загружаем персонажей…",
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        } else if (characters.isEmpty() && !isLoading) {
            Text(
                text = "База данных пуста. Проверьте интернет и нажмите «Обновить».",
                color = Color.LightGray,
                modifier = Modifier.padding(20.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(characters, key = { it.id }) { character ->
                    CharacterCard(character = character)
                }
                item {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = greenColor,
                            modifier = Modifier.padding(16.dp)
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
