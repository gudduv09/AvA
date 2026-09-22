package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.ui.navigation.AvaApp
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getDatabase(this)

    setContent {
      AvaTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(AvaBackground)
        ) {
          AvaApp(database = database)
        }
      }
    }
  }
}

