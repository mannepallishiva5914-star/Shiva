package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.SupportedLanguage
import com.example.ui.theme.ConfidenceGreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSubtleBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextOnCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningRed

/**
 * Transliteration Engine Section Header & Detected Spoken Audio Card
 */
@Composable
fun TransliterationEngineCard(
    language: SupportedLanguage,
    confidenceScore: Float,
    onSwitchLanguageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header Row: "Transliteration Engine" + "PRO PRECISION" Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Transliteration Engine",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // "PRO PRECISION" Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x2200E5FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.6f))
            ) {
                Text(
                    text = "PRO PRECISION",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Detected Spoken Audio Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = DarkCardSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Top Mini Row: Mic Icon + "DETECTED SPOKEN AUDIO" + "Switch v" button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Microphone",
                            tint = CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DETECTED SPOKEN AUDIO",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }

                    // "Switch v" button from screenshot
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onSwitchLanguageClick)
                            .testTag("switch_language_button"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E2837),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSubtleBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Switch",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Language Name Title (e.g. "Telugu (తెలుగు)")
                Text(
                    text = "${language.name} (${language.nativeScript})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Confidence Score Line
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(ConfidenceGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${String.format("%.1f", confidenceScore * 100)}% Confidence Score",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = ConfidenceGreen
                    )
                }
            }
        }
    }
}

/**
 * Phonetic Transliteration Mode Card with Speech Sound Matrix (Exact Stitch Design)
 */
@Composable
fun PhoneticTransliterationModeCard(
    language: SupportedLanguage,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = DarkCardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with rounded blue/cyan speaker badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F3247))
                        .border(1.dp, CyanAccent.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Phonetic Audio",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Phonetic Transliteration Mode",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "SOUND-PRESERVING ACTIVE",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Explanation text
            Text(
                text = "Captions are transcribed in Latin/English script representing genuine spoken acoustic sounds, never translated meanings.",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // SPEECH SOUND MATRIX Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0B1019),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2536))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Matrix Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SPEECH SOUND MATRIX",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = "LITERAL SOUNDS ONLY",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 1: Spoken Dialogue
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF141C2A)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Spoken Dialogue",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextSecondary
                            )
                            Text(
                                text = "\"${language.sampleSpoken}\"",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Row 2: Output Script (Glowing Cyan with Green Check)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF141F30),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Correct",
                                    tint = ConfidenceGreen,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Output Script",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = ConfidenceGreen
                                )
                            }
                            Text(
                                text = "\"${language.sampleOutputScript}\"",
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Row 3: Not Translation (Red Cross with Strikethrough/Dim text)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF121822)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Not Translation",
                                    tint = WarningRed,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Not Translation",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = WarningRed.copy(alpha = 0.85f)
                                )
                            }
                            Text(
                                text = "\"${language.forbiddenTranslation}\"",
                                fontSize = 12.sp,
                                textDecoration = TextDecoration.LineThrough,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info note at bottom of card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Note",
                    tint = TextMuted,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 1.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "English dialogue segments will be transcribed verbatim. Non-Latin scripts are rendered phonetically for universal readability across Reels/Shorts.",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = TextMuted
                )
            }
        }
    }
}

/**
 * Primary Floating Action Button (Glowing Neon Cyan Pill from Screenshot)
 */
@Composable
fun GenerateCaptionsCtaButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(27.dp), ambientColor = NeonCyan, spotColor = NeonCyan)
                .testTag("generate_captions_button"),
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyan,
                contentColor = TextOnCyan
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Subtitles,
                    contentDescription = "Captions",
                    tint = TextOnCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isLoading) "Generating Audio Captions..." else "Generate Offline Captions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnCyan
                )
                if (!isLoading) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0x33000000)
                    ) {
                        Text(
                            text = "~14s",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TextOnCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // GPU Acceleration info line from Stitch screenshot
        Text(
            text = "GPU Core: Qualcomm Adreno Local Fast-Int8 Quantized",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            color = TextMuted
        )
    }
}
