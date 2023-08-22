package id.teman.app.mitra.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimensions(
    private val multiplier: Double,
    val size_0dp: Dp = (multiplier * 0).dp,
    val size_1dp: Dp = (multiplier * 1).dp,
    val size_2dp: Dp = (multiplier * 2).dp,
    val size_4dp: Dp = (multiplier * 4).dp,
    val size_6dp: Dp = (multiplier * 6).dp,
    val size_8dp: Dp = (multiplier * 8).dp,
    val size_10dp: Dp = (multiplier * 10).dp,
    val size_12dp: Dp = (multiplier * 12).dp,
    val size_14dp: Dp = (multiplier * 14).dp,
    val size_16dp: Dp = (multiplier * 16).dp,
    val size_18dp: Dp = (multiplier * 18).dp,
    val size_20dp: Dp = (multiplier * 20).dp,
    val size_22dp: Dp = (multiplier * 22).dp,
    val size_24dp: Dp = (multiplier * 24).dp,
    val size_26dp: Dp = (multiplier * 26).dp,
    val size_28dp: Dp = (multiplier * 28).dp,
    val size_30dp: Dp = (multiplier * 30).dp,
    val size_32dp: Dp = (multiplier * 32).dp,
    val size_36dp: Dp = (multiplier * 36).dp,
    val size_38dp: Dp = (multiplier * 38).dp,
    val size_40dp: Dp = (multiplier * 40).dp,
    val size_44dp: Dp = (multiplier * 44).dp,
    val size_48dp: Dp = (multiplier * 48).dp,
    val size_50dp: Dp = (multiplier * 50).dp,
    val size_52dp: Dp = (multiplier * 52).dp,
    val size_56dp: Dp = (multiplier * 56).dp,
    val size_60dp: Dp = (multiplier * 60).dp,
    val size_64dp: Dp = (multiplier * 64).dp,
    val size_68dp: Dp = (multiplier * 68).dp,
    val size_72dp: Dp = (multiplier * 72).dp,
    val size_80dp: Dp = (multiplier * 80).dp,
    val size_96dp: Dp = (multiplier * 96).dp,
    val size_100dp: Dp = (multiplier * 100).dp,
    val size_112dp: Dp = (multiplier * 112).dp,
    val size_120dp: Dp = (multiplier * 120).dp,
    val size_128dp: Dp = (multiplier * 128).dp,
    val size_144dp: Dp = (multiplier * 144).dp,
    val size_146dp: Dp = (multiplier * 146).dp,
    val size_160dp: Dp = (multiplier * 160).dp,
    val size_200dp: Dp = (multiplier * 200).dp,
    val size_375dp: Dp = (multiplier * 375).dp
)

internal val NormalDimension = Dimensions(multiplier = 1.0)
internal val SmallDimension = Dimensions(multiplier = 0.875)

@Composable
internal fun ProvideAppDimension(dimensions: Dimensions, content: @Composable () -> Unit) {
    val appDimension = remember { dimensions }
    CompositionLocalProvider(LocalAppDimension provides appDimension, content = content)
}

internal val LocalAppDimension = staticCompositionLocalOf { NormalDimension }