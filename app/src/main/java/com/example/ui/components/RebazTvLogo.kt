package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RebazTvLogo(
    modifier: Modifier = Modifier,
    heightDp: Int = 55 // لێرەدا قەبارەکەیمان گەورە کردووە بۆ 55 تا بە جوانی دەربکەوێت
) {
    val context = LocalContext.current
    val customDrawableId = remember {
        // سەرەتا بەدوای logo_wide دا دەگەڕێت
        val id1 = context.resources.getIdentifier("logo_wide", "drawable", context.packageName)
        if (id1 != 0) id1 else context.resources.getIdentifier("logo", "drawable", context.packageName)
    }

    if (customDrawableId != 0) {
        Image(
            painter = painterResource(id = customDrawableId),
            contentDescription = "REBAZ TV",
            modifier = modifier
                .height(heightDp.dp)
                .padding(start = 4.dp), // کەمێک بۆشایی بۆ ئەوەی نەنوسێت بە لێوارەکەوە
            contentScale = ContentScale.Fit // ئەمە وادەکات پانییەکەی بە شێوەیەکی ئۆتۆماتیکی و ڕێکخراو گەورە بێت
        )
    } else {
        RebazTvVectorLogo(
            modifier = modifier,
            scale = heightDp / 42f
        )
    }
}

@Composable
fun RebazTvVectorLogo(
    modifier: Modifier = Modifier,
    scale: Float = 1.0f
) {
    Row(
        modifier = modifier
            .height((44 * scale).dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((8 * scale).dp)
    ) {
        EmblemBadge(size = (42 * scale).dp)

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "REBAZ",
                style = TextStyle(
                    fontSize = (22 * scale).sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (2.0 * scale).sp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFE2E8F0),
                            Color(0xFF94A3B8),
                            Color(0xFFCBD5E1),
                            Color(0xFF64748B)
                        )
                    )
                ),
                modifier = Modifier.offset(y = (2 * scale).dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.offset(y = (-2 * scale).dp)
            ) {
                PulseWaveform(
                    color = Color(0xFF00E5FF),
                    isLeft = true,
                    modifier = Modifier
                        .width((32 * scale).dp)
                        .height((16 * scale).dp)
                )

                Spacer(modifier = Modifier.width((4 * scale).dp))

                Text(
                    text = "TV",
                    style = TextStyle(
                        fontSize = (15 * scale).sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = (1.5 * scale).sp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF),
                                Color(0xFFCBD5E1),
                                Color(0xFF94A3B8),
                                Color(0xFF64748B)
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.width((4 * scale).dp))

                PulseWaveform(
                    color = Color(0xFFFF9100),
                    isLeft = false,
                    modifier = Modifier
                        .width((32 * scale).dp)
                        .height((16 * scale).dp)
                )
            }
        }
    }
}

@Composable
private fun EmblemBadge(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w / 2f, h / 2f)
            val radius = w / 2f - 3.dp.toPx()

            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFF94A3B8),
                        Color(0xFFE2E8F0),
                        Color(0xFF334155),
                        Color(0xFFCBD5E1)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                ),
                radius = radius + 2.dp.toPx(),
                center = center
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E3A8A),
                        Color(0xFF0F172A),
                        Color(0xFF030712)
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            val nodes = listOf(
                Offset(w * 0.35f, h * 0.35f),
                Offset(w * 0.65f, h * 0.32f),
                Offset(w * 0.28f, h * 0.62f),
                Offset(w * 0.68f, h * 0.65f),
                Offset(w * 0.50f, h * 0.50f)
            )

            val linePaint = Color(0x6638BDF8)
            drawLine(linePaint, nodes[0], nodes[1], strokeWidth = 1.dp.toPx())
            drawLine(linePaint, nodes[0], nodes[4], strokeWidth = 1.dp.toPx())
            drawLine(linePaint, nodes[1], nodes[4], strokeWidth = 1.dp.toPx())
            drawLine(linePaint, nodes[2], nodes[4], strokeWidth = 1.dp.toPx())
            drawLine(linePaint, nodes[3], nodes[4], strokeWidth = 1.dp.toPx())
            drawLine(linePaint, nodes[2], nodes[3], strokeWidth = 1.dp.toPx())

            for (node in nodes) {
                drawCircle(Color(0xFF38BDF8), radius = 1.8.dp.toPx(), center = node)
                drawCircle(Color.White, radius = 0.9.dp.toPx(), center = node)
            }

            val chevronPath = Path().apply {
                moveTo(w * 0.25f, h * 0.72f)
                lineTo(w * 0.42f, h * 0.48f)
                lineTo(w * 0.47f, h * 0.55f)
                lineTo(w * 0.34f, h * 0.72f)
                close()
            }
            drawPath(
                path = chevronPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0xFFCBD5E1), Color(0xFF64748B))
                )
            )

            val arrowPath = Path().apply {
                moveTo(w * 0.22f, h * 0.76f)
                lineTo(w * 0.62f, h * 0.34f)
                lineTo(w * 0.56f, h * 0.26f)
                lineTo(w * 0.88f, h * 0.16f)
                lineTo(w * 0.78f, h * 0.48f)
                lineTo(w * 0.70f, h * 0.42f)
                lineTo(w * 0.30f, h * 0.84f)
                close()
            }

            drawPath(
                path = arrowPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00E5FF),
                        Color(0xFF0091EA),
                        Color(0xFF38BDF8),
                        Color(0xFFFFFFFF)
                    ),
                    start = Offset(w * 0.2f, h * 0.8f),
                    end = Offset(w * 0.9f, h * 0.1f)
                )
            )

            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        Color(0x0000B0FF),
                        Color(0xFF00B0FF),
                        Color(0xFF00E5FF),
                        Color(0x0000B0FF)
                    )
                ),
                startAngle = 100f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = Offset(1.dp.toPx(), 1.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(w - 2.dp.toPx(), h - 2.dp.toPx()),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            val arcCenter = Offset(w * 0.50f, h * 0.24f)
            drawCircle(Color.White, radius = 1.2.dp.toPx(), center = arcCenter)
            drawArc(
                color = Color(0xFF38BDF8),
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(arcCenter.x - 4.dp.toPx(), arcCenter.y - 4.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(8.dp.toPx(), 8.dp.toPx()),
                style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = Color.White,
                startAngle = 205f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = Offset(arcCenter.x - 7.dp.toPx(), arcCenter.y - 7.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(14.dp.toPx(), 14.dp.toPx()),
                style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun PulseWaveform(
    color: Color,
    isLeft: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val midY = h * 0.5f

        val path = Path().apply {
            if (isLeft) {
                moveTo(0f, midY)
                lineTo(w * 0.35f, midY)
                lineTo(w * 0.42f, midY + h * 0.25f)
                lineTo(w * 0.55f, midY - h * 0.45f)
                lineTo(w * 0.68f, midY + h * 0.45f)
                lineTo(w * 0.80f, midY - h * 0.20f)
                lineTo(w * 0.88f, midY)
                lineTo(w, midY)
            } else {
                moveTo(0f, midY)
                lineTo(w * 0.12f, midY)
                lineTo(w * 0.20f, midY - h * 0.20f)
                lineTo(w * 0.32f, midY + h * 0.45f)
                lineTo(w * 0.45f, midY - h * 0.45f)
                lineTo(w * 0.58f, midY + h * 0.25f)
                lineTo(w * 0.65f, midY)
                lineTo(w, midY)
            }
        }

        drawPath(
            path = path,
            color = color.copy(alpha = 0.4f),
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(
                    color,
                    Color.White,
                    color
                )
            ),
            style = Stroke(
                width = 1.8.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
