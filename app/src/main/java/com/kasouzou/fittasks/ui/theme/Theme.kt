package com.kasouzou.fittasks.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
        darkColorScheme(
                primary = SoftPinkDark,
                secondary = SoftBlueDark,
                tertiary = PastelPurple, // tertiary -- ラテン語で「3つずつ」や「3番目の」を意味する
                surface = DeepGrey, // タスクグループカードの背景色等に用いられる。
        )

private val LightColorScheme =
        lightColorScheme(
                primary = SoftPink,
                secondary = SoftBlue,
                tertiary = PastelGreen,
                background = SoftPinkLight,
                surface = Color.White,
                onPrimary = Color.White,
                onSecondary = Color.Black,
                onTertiary = Color.Black,
                onBackground = Color(0xFF1C1B1F),
                onSurface = Color(0xFF1C1B1F),
        )

@Composable
fun FitTasksTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        // Dynamic color is available on Android 12+
        dynamicColor: Boolean = false,
        content: @Composable () -> Unit
) {
    val colorScheme =
            when {
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (darkTheme) dynamicDarkColorScheme(context)
                    else dynamicLightColorScheme(context)
                }
                darkTheme -> DarkColorScheme
                else -> LightColorScheme
            }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

