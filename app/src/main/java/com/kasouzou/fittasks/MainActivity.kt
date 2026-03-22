package com.kasouzou.fittasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kasouzou.fittasks.ui.TaskListScreen
import com.kasouzou.fittasks.ui.theme.FitTasksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitTasksTheme {
                TaskListScreen()
            }
        }
    }
}