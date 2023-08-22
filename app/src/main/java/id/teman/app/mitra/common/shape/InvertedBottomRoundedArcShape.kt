package id.teman.app.mitra.common.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class InvertedBottomRoundedArcShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = drawArcPath(size = size)
        )
    }

    fun drawArcPath(size: Size): Path {
        return Path().apply {
            reset()

            // go from (0,0) to (width, 0)
            lineTo(size.width, 0f)

            // go from (width, 0) to (width, height)
            lineTo(size.width, size.height)

            // Draw an arch from (width, height) to (0, height)
            // starting from 0 degree to 180 degree
            arcTo(
                rect =
                Rect(
                    Offset(0f, size.height * 0.6f),
                    Size(size.width, size.height * 0.6f)
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )

            // go from (0, height) to (0, 0)
            lineTo(0f, 0f)
            close()
        }
    }
}