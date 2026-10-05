package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CaptionBgStyle
import com.example.data.model.CaptionFont
import com.example.data.model.CaptionSegment
import com.example.data.model.CaptionStyle
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.ConfidenceGreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSubtleBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.sin

/**
 * Top bar matching the exact Stitch design:
 * Dashed-border back arrow, Screen Title, Green indicator toggle pill, Profile icon button
 */
@Composable
fun StitchTopBar(
    title: String,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    showBack: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (showBack) {
                // Dashed circular border button from Stitch reference
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .drawBehind {
                            val strokeWidth = 1.5.dp.toPx()
                            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                            drawCircle(
                                color = NeonCyan.copy(alpha = 0.5f),
                                radius = size.minDimension / 2 - strokeWidth / 2,
                                style = Stroke(width = strokeWidth, pathEffect = dashPathEffect)
                            )
                        }
                        .clip(CircleShape)
                        .clickable(onClick = onBackClick)
                        .testTag("top_bar_back_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Status Indicator Pill with glowing green dot
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkCardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(ConfidenceGreen)
                    )
                }
            }

            // User / Settings Avatar Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB5EAD7)) // Mint light background as in reference
                    .clickable(onClick = onProfileClick)
                    .testTag("top_bar_profile_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Settings & Projects",
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/**
 * Video Preview Card showing realistic video frame with synchronized caption overlay
 */
@Composable
fun VideoPreviewCard(
    currentCaption: CaptionSegment?,
    captionStyle: CaptionStyle,
    isPlaying: Boolean,
    currentTimeMs: Long,
    durationMs: Long,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0A0F18),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onTogglePlay)
                .testTag("video_preview_box")
        ) {
            // Simulated Cinematic Video Backdrop (dark moody lighting like the screenshot)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Dark moody vignette
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF26334D), Color(0xFF0D131F), Color(0xFF06090E)),
                        center = Offset(w * 0.5f, h * 0.4f),
                        radius = w * 0.8f
                    )
                )

                // Atmospheric warm cinematic glow on subject (like leather jacket actor in screenshot)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x35FF9100), Color(0x00FF9100)),
                        center = Offset(w * 0.52f, h * 0.35f),
                        radius = h * 0.55f
                    )
                )
            }

            // Central Play/Pause Micro-indicator (when paused)
            if (!isPlaying) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(Color(0x88000000))
                        .border(1.dp, NeonCyan.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Caption overlay positioned dynamically according to CaptionStyle
            val displayText = currentCaption?.phoneticText?.takeIf { it.isNotBlank() } ?: "\"Thaggedhele...\""
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = when {
                    captionStyle.positionRatioY < 0.3f -> Alignment.TopCenter
                    captionStyle.positionRatioY > 0.7f -> Alignment.BottomCenter
                    else -> Alignment.Center
                }
            ) {
                val paddingModifier = when {
                    captionStyle.positionRatioY < 0.3f -> Modifier.padding(top = 16.dp)
                    captionStyle.positionRatioY > 0.7f -> Modifier.padding(bottom = 16.dp)
                    else -> Modifier
                }

                CaptionRenderer(
                    text = displayText,
                    style = captionStyle,
                    modifier = paddingModifier
                )
            }

            // Video Duration & Scrub time mini pill
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xAA0A0F18)
            ) {
                Text(
                    text = "${CaptionSegment.formatMs(currentTimeMs)} / ${CaptionSegment.formatMs(durationMs)}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = AccentOrange,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}

/**
 * Renders caption text with the selected font, size, color, background and outline
 */
@Composable
fun CaptionRenderer(
    text: String,
    style: CaptionStyle,
    modifier: Modifier = Modifier
) {
    val fontFamily = when (style.fontChoice) {
        CaptionFont.CINEMATIC_SANS -> FontFamily.SansSerif
        CaptionFont.BOLD_IMPACT -> FontFamily.SansSerif
        CaptionFont.SUB_MONOSPACE -> FontFamily.Monospace
        CaptionFont.EDITORIAL_SERIF -> FontFamily.Serif
        CaptionFont.REELS_NEON -> FontFamily.Cursive
    }

    val fontWeight = when (style.fontChoice) {
        CaptionFont.BOLD_IMPACT -> FontWeight.Black
        CaptionFont.CINEMATIC_SANS -> FontWeight.SemiBold
        CaptionFont.SUB_MONOSPACE -> FontWeight.Bold
        CaptionFont.EDITORIAL_SERIF -> FontWeight.Medium
        CaptionFont.REELS_NEON -> FontWeight.ExtraBold
    }

    val textColor = Color(style.textColorHex)

    val boxModifier = when (style.bgStyle) {
        CaptionBgStyle.NONE -> modifier
        CaptionBgStyle.TRANSLUCENT -> modifier
            .background(Color(0xBB0A0F18), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        CaptionBgStyle.SOLID -> modifier
            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        CaptionBgStyle.OUTLINE -> modifier
            .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
            .background(Color(0x55000000), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        CaptionBgStyle.NEON_GLOW -> modifier
            .border(1.5.dp, NeonCyan, RoundedCornerShape(8.dp))
            .background(Color(0x99031622), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    }

    Box(modifier = boxModifier) {
        Text(
            text = text,
            color = textColor,
            fontSize = style.fontSizeSp.sp,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            letterSpacing = if (style.fontChoice == CaptionFont.BOLD_IMPACT) 0.5.sp else 0.sp
        )
    }
}

/**
 * Interactive Audio Waveform Scrubber & Spec Box (Matching Stitch design)
 */
@Composable
fun AudioWaveformTrackCard(
    title: String,
    durationMs: Long,
    currentPositionMs: Long,
    sampleRate: String,
    channels: String,
    vocalBandStatus: String,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = DarkCardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row: Waveform icon + Title + Duration (Glowing Orange)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Audio Track",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = CaptionSegment.formatMs(durationMs),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Waveform Visualizer Canvas with interactive scrubber
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF090E17))
            ) {
                val totalWidth = maxWidth
                val progress = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(durationMs) {
                            detectTapGestures { offset ->
                                val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                                onSeek((fraction * durationMs).toLong())
                            }
                        }
                        .pointerInput(durationMs) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                                onSeek((fraction * durationMs).toLong())
                            }
                        }
                ) {
                    val barCount = 42
                    val barWidth = 3.5.dp.toPx()
                    val spacing = (size.width - (barCount * barWidth)) / (barCount - 1)
                    val centerY = size.height / 2f
                    val currentScrubberX = progress * size.width

                    for (i in 0 until barCount) {
                        val x = i * (barWidth + spacing)
                        // Acoustic sine wave amplitude variation
                        val rawFactor = (sin(i * 0.45f) * 0.38f + sin(i * 0.95f) * 0.32f + 0.30f).coerceIn(0.18f, 0.95f)
                        val barHeight = size.height * rawFactor

                        val isPassed = x <= currentScrubberX
                        val barColor = if (isPassed) NeonCyan else Color(0xFF2A374A)

                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, centerY - barHeight / 2f),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                    }

                    // Scrubber vertical cyan line with glow
                    drawLine(
                        color = CyanAccent,
                        start = Offset(currentScrubberX, 0f),
                        end = Offset(currentScrubberX, size.height),
                        strokeWidth = 3.dp.toPx()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tech Specs Footer Row (matching Stitch design)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$sampleRate • $channels",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(ConfidenceGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = vocalBandStatus,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = ConfidenceGreen
                    )
                }
            }
        }
    }
}
