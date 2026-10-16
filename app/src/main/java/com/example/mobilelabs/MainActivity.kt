package com.example.mobilelabs

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilelabs.ui.theme.MobileLabsTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

private object AppRoute {
    const val START = "start"
    const val REGISTRATION = "registration"
    const val CONTENT = "content"
    const val SETTINGS = "settings"
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "MainActivity: onCreate")

        CharacterRepository.loadBackupIfAvailable(this)

        setContent {
            MobileLabsTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = AppRoute.START
                ) {
                    composable(AppRoute.START) {
                        ScreenLifecycleLogger("StartFragment")
                        StartScreen(
                            onRegistrationClick = {
                                navController.navigate(AppRoute.REGISTRATION)
                            },
                            onLoginClick = {
                                navController.navigate(AppRoute.CONTENT) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(AppRoute.REGISTRATION) {
                        ScreenLifecycleLogger("RegistrationFragment")
                        RegistrationScreen(
                            onRegistrationComplete = {
                                // Экран регистрации возвращает пользователя на стартовый экран.
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(AppRoute.CONTENT) {
                        ScreenLifecycleLogger("ContentFragment")
                        ContentScreen(
                            onSettingsClick = {
                                navController.navigate(AppRoute.SETTINGS)
                            }
                        )
                    }

                    composable(AppRoute.SETTINGS) {
                        ScreenLifecycleLogger("SettingsFragment")
                        SettingsScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "MainActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "MainActivity: onResume")
    }

    override fun onPause() {
        Log.d("Lifecycle", "MainActivity: onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("Lifecycle", "MainActivity: onStop")
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Lifecycle", "MainActivity: onRestart")
    }

    override fun onDestroy() {
        Log.d("Lifecycle", "MainActivity: onDestroy")
        super.onDestroy()
    }
}

@Composable
private fun ScreenLifecycleLogger(screenName: String) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, screenName) {
        val observer = LifecycleEventObserver { _, event ->
            if (event != Lifecycle.Event.ON_ANY) {
                Log.d("Lifecycle", "$screenName: ${event.name}")
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
