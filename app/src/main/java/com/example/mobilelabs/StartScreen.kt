package com.example.mobilelabs

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartScreen(
    onRegistrationClick: () -> Unit,
    onLoginClick: () -> Unit
) {

    val context = LocalContext.current
    val preferences = remember {
        context.getSharedPreferences("rick_morty_preferences", Context.MODE_PRIVATE)
    }
    var login by remember {
        mutableStateOf(preferences.getString("username", "") ?: "")
    }

    var password by remember {
        mutableStateOf("")
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, preferences) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val savedUsername = preferences.getString("username", "") ?: ""
                if (savedUsername.isNotBlank()) login = savedUsername
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val backgroundColor = Color(0xFF101118)
    val greenColor = Color(0xFF97CE4C)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(backgroundColor)
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🛸",
                fontSize = 52.sp
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "RICK & MORTY",
                color = greenColor,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "EXPLORER",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Explore characters across the multiverse",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(36.dp)
            )

            OutlinedTextField(
                value = login,
                onValueChange = {
                    login = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Username")
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = greenColor,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = greenColor,
                    cursorColor = greenColor
                )
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Password")
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = greenColor,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = greenColor,
                    cursorColor = greenColor
                )
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Button(
                onClick = {
                    when {
                        login.isBlank() -> Toast.makeText(context, "Enter username", Toast.LENGTH_SHORT).show()
                        password.isBlank() -> Toast.makeText(context, "Enter password", Toast.LENGTH_SHORT).show()
                        preferences.contains("username") &&
                            (login != preferences.getString("username", "") ||
                                password != preferences.getString("password", "")) ->
                            Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show()
                        else -> onLoginClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = greenColor,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "ENTER",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            TextButton(
                onClick = onRegistrationClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Create account",
                    color = greenColor
                )
            }
        }
    }
}