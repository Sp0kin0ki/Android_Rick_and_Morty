package com.example.mobilelabs

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember {
        context.getSharedPreferences("rick_morty_preferences", Context.MODE_PRIVATE)
    }
    val settings by UserSettingsStore.observe(context).collectAsState(initial = UserSettings())
    var username by remember { mutableStateOf(preferences.getString("username", "") ?: "") }
    var notifications by remember { mutableStateOf(preferences.getBoolean("notifications", true)) }
    var showUsernameDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var usernameInput by remember { mutableStateOf(username) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var backupInfo by remember { mutableStateOf(ExternalBackupManager.info(context)) }
    var privateBackupExists by remember { mutableStateOf(ExternalBackupManager.hasPrivateBackup(context)) }

    val bg = Color(0xFF101118)
    val green = Color(0xFF97CE4C)
    val white = Color.White
    val muted = Color.LightGray

    Column(
        Modifier.fillMaxSize().statusBarsPadding().background(bg)
            .verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        Text("SETTINGS", color = green, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        Text("Profile", color = white, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Username: ${username.ifBlank { "не задан" }}", color = muted)
        Button(
            onClick = { usernameInput = username; showUsernameDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = green, contentColor = Color.Black)
        ) { Text("CHANGE USERNAME") }
        TextButton(onClick = {
            newPassword = ""; confirmPassword = ""; showPasswordDialog = true
        }, modifier = Modifier.fillMaxWidth()) { Text("CHANGE PASSWORD", color = green) }

        HorizontalDivider(Modifier.padding(vertical = 16.dp), color = Color.DarkGray)
        Text("SharedPreferences", color = white, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Username и уведомления сохраняются в SharedPreferences.", color = muted, fontSize = 13.sp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Notifications", color = white)
                Text("Receive application notifications", color = muted, fontSize = 12.sp)
            }
            Switch(checked = notifications, onCheckedChange = {
                notifications = it
                preferences.edit { putBoolean("notifications", it) }
            })
        }

        HorizontalDivider(Modifier.padding(vertical = 16.dp), color = Color.DarkGray)
        Text("DataStore", color = white, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Тема, размер шрифта и язык сохраняются асинхронно через Preferences DataStore.", color = muted, fontSize = 13.sp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Dark theme", color = white)
            Switch(checked = settings.darkTheme, onCheckedChange = { value ->
                scope.launch { UserSettingsStore.setDarkTheme(context, value) }
            })
        }
        Text("Размер шрифта: ${settings.fontScale}%", color = white)
        Slider(
            value = settings.fontScale.toFloat(),
            onValueChange = { value -> scope.launch { UserSettingsStore.setFontScale(context, value.toInt()) } },
            valueRange = 80f..140f,
            steps = 5
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Язык", color = white)
            Row {
                TextButton(onClick = { scope.launch { UserSettingsStore.setLanguage(context, "Русский") } }) {
                    Text(if (settings.language == "Русский") "✓ Русский" else "Русский", color = green)
                }
                TextButton(onClick = { scope.launch { UserSettingsStore.setLanguage(context, "English") } }) {
                    Text(if (settings.language == "English") "✓ English" else "English", color = green)
                }
            }
        }
        Text("Сейчас выбран: ${settings.language}", color = muted, fontSize = 12.sp)

        HorizontalDivider(Modifier.padding(vertical = 16.dp), color = Color.DarkGray)
        Text("External Storage backup", color = white, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(
            if (backupInfo.exists) "Файл: ${backupInfo.name}\nПуть: ${backupInfo.location}\nРазмер: ${ExternalBackupManager.formatSize(backupInfo.sizeBytes)}\nИзменён: ${ExternalBackupManager.formatDate(backupInfo.modifiedAt)}"
            else "Файл в Documents пока отсутствует.",
            color = if (backupInfo.exists) green else muted,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                ExternalBackupManager.create(context).onSuccess {
                    backupInfo = ExternalBackupManager.info(context)
                    privateBackupExists = ExternalBackupManager.hasPrivateBackup(context)
                    Toast.makeText(context, "Резервная копия сохранена в Documents/RickAndMorty", Toast.LENGTH_LONG).show()
                }.onFailure {
                    Toast.makeText(context, it.message ?: "Не удалось создать резервную копию", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = green, contentColor = Color.Black)
        ) { Text("CREATE .TXT BACKUP") }

        TextButton(
            enabled = backupInfo.exists,
            onClick = {
                ExternalBackupManager.deletePublicAndKeepPrivateCopy(context).onSuccess { deleted ->
                    backupInfo = ExternalBackupManager.info(context)
                    privateBackupExists = ExternalBackupManager.hasPrivateBackup(context)
                    Toast.makeText(context, if (deleted) "Файл удалён; копия сохранена внутри приложения" else "Файл не найден", Toast.LENGTH_LONG).show()
                }.onFailure {
                    Toast.makeText(context, it.message ?: "Ошибка удаления", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("DELETE FILE (KEEP PRIVATE COPY)", color = Color(0xFFFF8A80)) }

        Text(
            if (privateBackupExists) "Внутренняя резервная копия доступна." else "Внутренней резервной копии пока нет.",
            color = if (privateBackupExists) green else muted,
            fontSize = 13.sp
        )
        TextButton(
            enabled = privateBackupExists,
            onClick = {
                ExternalBackupManager.restore(context).onSuccess { restored ->
                    backupInfo = ExternalBackupManager.info(context)
                    Toast.makeText(context, if (restored) "Файл восстановлен в Documents/RickAndMorty" else "Резервная копия не найдена", Toast.LENGTH_LONG).show()
                }.onFailure {
                    Toast.makeText(context, it.message ?: "Ошибка восстановления", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("RESTORE FILE", color = green) }

        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("BACK TO CHARACTERS", color = green)
        }
    }

    if (showUsernameDialog) {
        AlertDialog(
            onDismissRequest = { showUsernameDialog = false },
            title = { Text("Change username") },
            text = {
                OutlinedTextField(
                    value = usernameInput, onValueChange = { usernameInput = it },
                    singleLine = true, label = { Text("New username") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val cleaned = usernameInput.trim()
                    if (cleaned.length >= 3) {
                        preferences.edit { putString("username", cleaned) }
                        username = cleaned
                        showUsernameDialog = false
                        Toast.makeText(context, "Username updated", Toast.LENGTH_SHORT).show()
                    } else Toast.makeText(context, "Username must contain at least 3 characters", Toast.LENGTH_SHORT).show()
                }) { Text("SAVE") }
            },
            dismissButton = { TextButton(onClick = { showUsernameDialog = false }) { Text("CANCEL") } }
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Change password") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPassword, onValueChange = { newPassword = it },
                        singleLine = true, label = { Text("New password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it },
                        singleLine = true, label = { Text("Confirm password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    when {
                        newPassword.length < 6 -> Toast.makeText(context, "Password must contain at least 6 characters", Toast.LENGTH_SHORT).show()
                        newPassword != confirmPassword -> Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        else -> {
                            preferences.edit { putString("password", newPassword) }
                            showPasswordDialog = false
                            Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) { Text("SAVE") }
            },
            dismissButton = { TextButton(onClick = { showPasswordDialog = false }) { Text("CANCEL") } }
        )
    }
}
