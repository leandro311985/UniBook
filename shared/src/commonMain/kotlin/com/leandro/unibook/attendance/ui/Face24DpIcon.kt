package com.leandro.unibook.attendance.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Face24DpIcon: ImageVector
    get() {
        if (_face24Dp != null) {
            return _face24Dp!!
        }
        val builder = ImageVector.Builder(
            name = "Face24Dp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        )
        builder.path(fill = SolidColor(Color(0xFF1F1F1F))) {
            moveTo(8.11f, 13.89f)
            quadTo(7.75f, 13.52f, 7.75f, 13f)
            quadTo(7.75f, 12.48f, 8.11f, 12.11f)
            quadTo(8.48f, 11.75f, 9f, 11.75f)
            quadTo(9.52f, 11.75f, 9.89f, 12.11f)
            quadTo(10.25f, 12.47f, 10.25f, 13f)
            quadTo(10.25f, 13.52f, 9.89f, 13.89f)
            quadTo(9.52f, 14.25f, 9f, 14.25f)
            quadTo(8.48f, 14.25f, 8.11f, 13.89f)
            close()
            moveTo(14.11f, 13.89f)
            quadTo(13.75f, 13.52f, 13.75f, 13f)
            quadTo(13.75f, 12.48f, 14.11f, 12.11f)
            quadTo(14.48f, 11.75f, 15f, 11.75f)
            quadTo(15.53f, 11.75f, 15.89f, 12.11f)
            quadTo(16.25f, 12.47f, 16.25f, 13f)
            quadTo(16.25f, 13.52f, 15.89f, 13.89f)
            quadTo(15.52f, 14.25f, 15f, 14.25f)
            quadTo(14.47f, 14.25f, 14.11f, 13.89f)
            close()
            moveTo(12f, 20f)
            quadTo(15.35f, 20f, 17.68f, 17.68f)
            quadTo(20f, 15.35f, 20f, 12f)
            quadTo(20f, 11.4f, 19.93f, 10.84f)
            quadTo(19.86f, 10.28f, 19.65f, 9.75f)
            quadTo(19.13f, 9.88f, 18.6f, 9.94f)
            quadTo(18.07f, 10f, 17.5f, 10f)
            quadTo(15.23f, 10f, 13.2f, 9.02f)
            quadTo(11.17f, 8.04f, 9.75f, 6.3f)
            quadTo(8.95f, 8.25f, 7.46f, 9.69f)
            quadTo(5.97f, 11.13f, 4f, 11.85f)
            quadTo(4f, 11.9f, 4f, 11.92f)
            quadTo(4f, 11.94f, 4f, 12f)
            quadTo(4f, 15.35f, 6.33f, 17.68f)
            quadTo(8.65f, 20f, 12f, 20f)
            close()
            moveTo(12f, 22f)
            quadTo(9.93f, 22f, 8.1f, 21.21f)
            quadTo(6.28f, 20.42f, 4.93f, 19.07f)
            quadTo(3.58f, 17.73f, 2.79f, 15.9f)
            quadTo(2f, 14.07f, 2f, 12f)
            quadTo(2f, 9.92f, 2.79f, 8.1f)
            quadTo(3.58f, 6.27f, 4.93f, 4.93f)
            quadTo(6.28f, 3.57f, 8.1f, 2.79f)
            quadTo(9.93f, 2f, 12f, 2f)
            quadTo(14.07f, 2f, 15.9f, 2.79f)
            quadTo(17.73f, 3.58f, 19.07f, 4.93f)
            quadTo(20.42f, 6.28f, 21.21f, 8.1f)
            quadTo(22f, 9.92f, 22f, 12f)
            quadTo(22f, 14.07f, 21.21f, 15.9f)
            quadTo(20.42f, 17.73f, 19.07f, 19.07f)
            quadTo(17.72f, 20.42f, 15.9f, 21.21f)
            quadTo(14.08f, 22f, 12f, 22f)
            close()
            moveTo(10.65f, 4.13f)
            quadTo(11.7f, 5.88f, 13.5f, 6.94f)
            quadTo(15.3f, 8f, 17.5f, 8f)
            quadTo(17.85f, 8f, 18.18f, 7.96f)
            quadTo(18.5f, 7.92f, 18.86f, 7.87f)
            quadTo(17.8f, 6.13f, 16f, 5.06f)
            quadTo(14.2f, 4f, 12f, 4f)
            quadTo(11.65f, 4f, 11.33f, 4.04f)
            quadTo(11.01f, 4.08f, 10.65f, 4.13f)
            close()
            moveTo(4.43f, 9.48f)
            quadTo(5.7f, 8.75f, 6.65f, 7.6f)
            quadTo(7.6f, 6.45f, 8.08f, 5.02f)
            quadTo(6.8f, 5.75f, 5.85f, 6.9f)
            quadTo(4.9f, 8.05f, 4.43f, 9.48f)
            close()
        }
        _face24Dp = builder.build()
        return _face24Dp!!
    }

private var _face24Dp: ImageVector? = null
