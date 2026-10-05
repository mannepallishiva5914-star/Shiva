package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CaptionBgStyle
import com.example.data.model.CaptionFont
import com.example.ui.components.StitchTopBar
import com.example.ui.components.VideoPreviewCard
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextOnCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CaptionViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaptionStylingScreen(
    viewModel: CaptionViewModel,
    onBackClick: () -> Unit,
    onNavigateToExport: () -> Unit
) {
    val durationMs by viewModel.durationMs.collectAsState()
    val currentPositionMs by viewModel.currentPositionMs.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val captionStyle by viewModel.captionStyle.collectAsState()

    val activeCaption = viewModel.getActiveCaptionAt(currentPositionMs)

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            StitchTopBar(
                title = "Caption Styling",
                onBackClick = onBackClick,
                onProfileClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                color = DarkCardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = onNavigateToExport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("apply_and_export_button"),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = TextOnCyan
                        )
                    ) {
                        Text(
                            text = "Save & Continue to Export",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Live Synchronized Video Preview Card
            VideoPreviewCard(
                currentCaption = activeCaption,
                captionStyle = captionStyle,
                isPlaying = isPlaying,
                currentTimeMs = currentPositionMs,
                durationMs = durationMs,
                onTogglePlay = { viewModel.togglePlayback() }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 1. EXACTLY 5 FONT CHOICES
            StylingSectionCard(title = "1. Font Typography (5 Choices)") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CaptionFont.values().forEach { font ->
                        val isSelected = captionStyle.fontChoice == font
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.updateFont(font) }
                                .testTag("font_choice_${font.id}"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFF142436) else Color(0xFF0F1522),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else Color(0xFF1C2738)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = font.displayName,
                                        fontSize = 15.sp,
                                        fontWeight = if (font == CaptionFont.BOLD_IMPACT) FontWeight.Black else FontWeight.Bold,
                                        fontFamily = when (font) {
                                            CaptionFont.SUB_MONOSPACE -> FontFamily.Monospace
                                            CaptionFont.EDITORIAL_SERIF -> FontFamily.Serif
                                            CaptionFont.REELS_NEON -> FontFamily.Cursive
                                            else -> FontFamily.SansSerif
                                        },
                                        color = if (isSelected) NeonCyan else TextPrimary
                                    )
                                    Text(
                                        text = font.description,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. FONT SIZE SELECTOR
            StylingSectionCard(title = "2. Font Size (${captionStyle.fontSizeSp} sp)") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sizes = listOf(16 to "Small", 20 to "Regular", 24 to "Large", 30 to "XL")
                    sizes.forEach { (sz, label) ->
                        val isSelected = captionStyle.fontSizeSp == sz
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateFontSize(sz) }
                                .testTag("font_size_$sz"),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NeonCyan else Color(0xFF0F1522),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else Color(0xFF1C2738))
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${sz}sp",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) TextOnCyan else TextPrimary
                                )
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = if (isSelected) TextOnCyan.copy(alpha = 0.8f) else TextMuted
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. TEXT COLOR PALETTE
            StylingSectionCard(title = "3. Text Color") {
                val colors = listOf(
                    0xFFFFFFFF to "White",
                    0xFF00F2FE to "Cyan",
                    0xFFFFE600 to "Yellow",
                    0xFF00E676 to "Green",
                    0xFFFF5722 to "Coral"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colors.forEach { (hex, name) ->
                        val isSelected = captionStyle.textColorHex == hex
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateTextColor(hex) }
                                .padding(4.dp)
                                .testTag("color_btn_$name")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(hex))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) NeonCyan else Color(0xFF334155),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (hex == 0xFFFFFFFF || hex == 0xFFFFE600) Color.Black else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                color = if (isSelected) NeonCyan else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. BACKGROUND / OUTLINE STYLE
            StylingSectionCard(title = "4. Background & Outline") {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CaptionBgStyle.values().forEach { bg ->
                        val isSelected = captionStyle.bgStyle == bg
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateBgStyle(bg) }
                                .testTag("bg_style_${bg.name}"),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF142436) else Color(0xFF0F1522),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else Color(0xFF1C2738))
                        ) {
                            Text(
                                text = bg.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonCyan else TextPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. CAPTION POSITION
            StylingSectionCard(title = "5. Caption Vertical Position") {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = listOf(
                            0.15f to "Top Header",
                            0.50f to "Center",
                            0.82f to "Bottom (Reels)"
                        )
                        presets.forEach { (ratio, label) ->
                            val isSelected = Math.abs(captionStyle.positionRatioY - ratio) < 0.05f
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.updatePosition(ratio) }
                                    .testTag("pos_preset_${label.take(4)}"),
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) NeonCyan else Color(0xFF0F1522),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else Color(0xFF1C2738))
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) TextOnCyan else TextPrimary,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Fine Tuning: ${(captionStyle.positionRatioY * 100).toInt()}% from top",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted
                    )

                    Slider(
                        value = captionStyle.positionRatioY,
                        onValueChange = { viewModel.updatePosition(it) },
                        valueRange = 0.10f..0.90f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = Color(0xFF1E2837)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("position_slider")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StylingSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = DarkCardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
