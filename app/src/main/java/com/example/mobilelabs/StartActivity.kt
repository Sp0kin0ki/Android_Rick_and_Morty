
package com.example.mobilelabs

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilelabs.ui.theme.MobileLabsTheme

class StartActivity : BaseActivity() {

    private var registeredUsername by mutableStateOf("")

    private val registrationLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                registeredUsername =
                    result.data?.getStringExtra(
                        RegistrationActivity.EXTRA_USERNAME
                    ).orEmpty()

                Toast.makeText(
                    this,
                    "Registration successful!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileLabsTheme {
                StartScreen(
                    initialLogin = registeredUsername,

                    onRegistrationClick = {
                        val intent = Intent(
                            this@StartActivity,
                            RegistrationActivity::class.java
                        )

                        registrationLauncher.launch(intent)
                    },

                    onLoginClick = {
                        val intent = Intent(
                            this@StartActivity,
                            ContentActivity::class.java
                        )

                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun StartScreen(
    initialLogin: String = "",
    onRegistrationClick: () -> Unit,
    onLoginClick: () -> Unit
) {

    var login by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    LaunchedEffect(initialLogin) {
        if (initialLogin.isNotEmpty()) {
            login = initialLogin
        }
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
                text = "🛸",
                fontSize = 52.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Explore characters across the multiverse",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

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

            Spacer(modifier = Modifier.height(14.dp))

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

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = onLoginClick,
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

            Spacer(modifier = Modifier.height(8.dp))

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