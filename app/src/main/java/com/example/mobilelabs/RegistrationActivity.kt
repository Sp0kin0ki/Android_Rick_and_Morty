package com.example.mobilelabs

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilelabs.ui.theme.MobileLabsTheme

class RegistrationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileLabsTheme {
                RegistrationScreen(
                    onRegistrationComplete = {
                        Toast.makeText(
                            this,
                            "Registration successful!",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun RegistrationScreen(
    onRegistrationComplete: () -> Unit
) {

    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    val backgroundColor = Color(0xFF101118)
    val greenColor = Color(0xFF97CE4C)

    Box(
        modifier = Modifier
            .fillMaxSize()
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
                text = "CREATE ACCOUNT",
                color = greenColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Join the multiverse",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    errorMessage = ""
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
                    errorMessage = ""
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
                modifier = Modifier.height(14.dp)
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Confirm password")
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
                modifier = Modifier.height(12.dp)
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF6B6B),
                    fontSize = 13.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

            Button(
                onClick = {

                    when {
                        username.length < 3 -> {
                            errorMessage =
                                "Username must contain at least 3 characters"
                        }

                        password.length < 6 -> {
                            errorMessage =
                                "Password must contain at least 6 characters"
                        }

                        password != confirmPassword -> {
                            errorMessage =
                                "Passwords do not match"
                        }

                        else -> {
                            onRegistrationComplete()
                        }
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
                    text = "CREATE ACCOUNT",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            TextButton(
                onClick = {
                    onRegistrationComplete()
                }
            ) {
                Text(
                    text = "Back to login",
                    color = greenColor
                )
            }
        }
    }
}