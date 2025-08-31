package kr.pe.gbpark.quizstream

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import kr.pe.gbpark.quizstream.navigation.QuizStreamNavigation
import kr.pe.gbpark.quizstream.ui.theme.QuizStreamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("QuizStream", "MainActivity onCreate called")
        enableEdgeToEdge()
        setContent {
            Log.d("QuizStream", "Setting QuizStreamTheme content")
            QuizStreamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Log.d("QuizStream", "Starting QuizStreamNavigation")
                    QuizStreamNavigation(navController = navController)
                }
            }
        }
    }
}