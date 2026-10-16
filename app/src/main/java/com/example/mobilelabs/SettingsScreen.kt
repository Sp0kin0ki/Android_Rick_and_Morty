package com.example.mobilelabs

import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val preferences = remember {
        context.getSharedPreferences("rick_morty_preferences", Context.MODE_PRIVATE)
    }
    var darkTheme by remember {
        mutableStateOf(preferences.getBoolean("dark_theme", true))
    }

    var notifications by remember {
        mutableStateOf(preferences.getBoolean("notifications", true))
    }
    var hasBackup by remember {
        mutableStateOf(CharacterRepository.hasBackup(context))
    }
    var username by remember {
        mutableStateOf(preferences.getString("username", "Morty") ?: "Morty")
    }
    var showUsernameDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var usernameInput by remember { mutableStateOf(username) }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    val backgroundColor = Color(0xFF101118)
    val greenColor = Color(0xFF97CE4C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "SETTINGS",
            color = greenColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Profile",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "Username: $username",
            color = Color.LightGray,
            fontSize = 15.sp
        )

        Text(
            text = "Status: Explorer",
            color = Color.LightGray,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                usernameInput = username
                showUsernameDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = greenColor,
                contentColor = Color.Black
            )
        ) {
            Text("CHANGE USERNAME")
        }

        TextButton(
            onClick = {
                newPassword = ""
                confirmNewPassword = ""
                showPasswordDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CHANGE PASSWORD", color = greenColor)
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Appearance",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "Dark theme",
                    color = Color.White
                )

                Text(
                    text = "Use dark colors",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Switch(
                checked = darkTheme,
                onCheckedChange = {
                    darkTheme = it
                    preferences.edit { putBoolean("dark_theme", it) }
                }
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "Notifications",
                    color = Color.White
                )

                Text(
                    text = "Receive application notifications",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Switch(
                checked = notifications,
                onCheckedChange = {
                    notifications = it
                    preferences.edit { putBoolean("notifications", it) }
                }
            )
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Text(
            text = "Backup",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = if (hasBackup) "Backup is available on this device" else "No backup created yet",
            color = if (hasBackup) greenColor else Color.Gray,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                CharacterRepository.createBackup(context)
                hasBackup = true
                Toast.makeText(context, "Character backup created", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = greenColor,
                contentColor = Color.Black
            )
        ) {
            Text("CREATE BACKUP")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            enabled = hasBackup,
            onClick = {
                val restored = CharacterRepository.restoreBackup(context)
                Toast.makeText(
                    context,
                    if (restored) "Backup restored: ${CharacterRepository.characters.size} characters" else "Backup could not be restored",
                    Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "RESTORE BACKUP",
                color = greenColor
            )
        }

        TextButton(
            enabled = hasBackup,
            onClick = {
                hasBackup = !CharacterRepository.deleteBackup(context)
                Toast.makeText(context, if (hasBackup) "Backup was not deleted" else "Backup deleted", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "DELETE BACKUP",
                color = Color(0xFFFF6B6B)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "BACK TO CHARACTERS",
                color = greenColor
            )
        }
    }

    if (showUsernameDialog) {
        AlertDialog(
            onDismissRequest = { showUsernameDialog = false },
            title = { Text("Change username") },
            text = {
                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = { usernameInput = it },
                    singleLine = true,
                    label = { Text("New username") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = greenColor,
                        focusedLabelColor = greenColor,
                        cursorColor = greenColor
                    )
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
                    } else {
                        Toast.makeText(context, "Username must contain at least 3 characters", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("SAVE", color = greenColor) }
            },
            dismissButton = {
                TextButton(onClick = { showUsernameDialog = false }) { Text("CANCEL") }
            }
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Change password") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        singleLine = true,
                        label = { Text("New password") },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = greenColor,
                            focusedLabelColor = greenColor,
                            cursorColor = greenColor
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        singleLine = true,
                        label = { Text("Confirm password") },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = greenColor,
                            focusedLabelColor = greenColor,
                            cursorColor = greenColor
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    when {
                        newPassword.length < 6 -> Toast.makeText(context, "Password must contain at least 6 characters", Toast.LENGTH_SHORT).show()
                        newPassword != confirmNewPassword -> Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        else -> {
                            preferences.edit { putString("password", newPassword) }
                            showPasswordDialog = false
                            Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) { Text("SAVE", color = greenColor) }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) { Text("CANCEL") }
            }
        )
    }
}