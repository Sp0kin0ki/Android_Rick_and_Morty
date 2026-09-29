package com.example.mobilelabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilelabs.ui.theme.MobileLabsTheme

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileLabsTheme {
                SettingsScreen(
                    onBack = {
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {

    var darkTheme by remember {
        mutableStateOf(true)
    }

    var notifications by remember {
        mutableStateOf(true)
    }

    val backgroundColor = Color(0xFF101118)
    val greenColor = Color(0xFF97CE4C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
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
            text = "Username: Morty",
            color = Color.LightGray,
            fontSize = 15.sp
        )

        Text(
            text = "Status: Explorer",
            color = Color.LightGray,
            fontSize = 15.sp
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

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

        Button(
            onClick = {
                // Создание резервной копии добавим позже
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
            onClick = {
                // Восстановление добавим позже
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "RESTORE BACKUP",
                color = greenColor
            )
        }

        TextButton(
            onClick = {
                // Удаление резервной копии добавим позже
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "DELETE BACKUP",
                color = Color(0xFFFF6B6B)
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

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
}