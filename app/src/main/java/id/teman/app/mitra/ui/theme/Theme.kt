package id.teman.app.mitra.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import id.teman.mitra.app.ui.theme.Purple200
import id.teman.mitra.app.ui.theme.Purple500
import id.teman.mitra.app.ui.theme.Purple700
import id.teman.mitra.app.ui.theme.Teal200

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun TemanUlaUserAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val configuration = LocalConfiguration.current
    val dimension = if (configuration.screenWidthDp <= 360) SmallDimension else NormalDimension

    ProvideAppDimension(dimensions = dimension) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object Theme {
//    val colors: Colors
//        @Composable
//        get() = LocalAppColors.current

//    val typography: Typography
//        @Composable
//        get() = LocalAppTypography.current

    val dimension: Dimensions
        @Composable
        get() = LocalAppDimension.current
}