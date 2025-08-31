package kr.pe.gbpark.quizstream.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue500,
    secondary = Blue600,
    tertiary = BlueAccent,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = TextOnBlue,
    onSecondary = TextOnBlue,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    secondary = Blue600,
    tertiary = BlueAccent,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onPrimary = TextOnBlue,
    onSecondary = TextOnBlue,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun QuizStreamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic color to use our blue theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}