package com.example.mobilelabs

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

open class BaseActivity : ComponentActivity() {

    private val tagName: String
        get() = this::class.simpleName ?: "Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "$tagName: onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "$tagName: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "$tagName: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "$tagName: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "$tagName: onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "$tagName: onDestroy")
    }
}