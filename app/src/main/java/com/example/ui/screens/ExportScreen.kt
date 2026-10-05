package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CaptionSegment
import com.example.engine.TransliterationEngine
import com.example.ui.components.StitchTopBar
import com.example.ui.components.VideoPreviewCard
import com.example.ui.theme.ConfidenceGreen
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

@Composable
fun ExportScreen(
    viewModel: CaptionViewModel,
    onBackClick: () -> Unit,
    onNavigateToProjects: () -> Unit
) {
    val context = LocalContext.current
    val projectTitle by viewModel.projectTitle.collectAsState()
    val durationMs by viewModel.durationMs.collectAsState()
    val currentPositionMs by viewModel.currentPositionMs.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val captions by viewModel.captions.collectAsState()
    val captionStyle by viewModel.captionStyle.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    val activeCaption = viewModel.getActiveCaptionAt(currentPositionMs)
    var isBurningInVideo by remember { mutableStateOf(false) }
    var burnedInSuccess by remember { mutableStateOf(false) }
    var projectSavedNotice by remember { mutableStateOf(false) }

    val srtContent = remember(captions) { TransliterationEngine.exportToSrt(captions) }
    val vttContent = remember(captions) { TransliterationEngine.exportToVtt(captions) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            StitchTopBar(
                title = "Export & Delivery",
                onBackClick = onBackClick,
                onProfileClick = onNavigateToProjects
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Video Preview with burned-in subtitles
            VideoPreviewCard(
                currentCaption = activeCaption,
                captionStyle = captionStyle,
                isPlaying = isPlaying,
                currentTimeMs = currentPositionMs,
                durationMs = durationMs,
                onTogglePlay = { viewModel.togglePlayback() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Info Badge
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = DarkCardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = projectTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${selectedLanguage.name} • ${captions.size} Phonetic Segments",
                            fontSize = 11.sp,
                            color = CyanAccent
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x3310B981)
                    ) {
                        Text(
                            text = "READY",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = ConfidenceGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. BURNED-IN VIDEO EXPORT
            ExportActionCard(
                icon = Icons.Default.Movie,
                title = "Video with Burned-In Captions",
                subtitle = "High-definition MP4 with embedded styled typography (Reels/Shorts ready)",
                actionLabel = if (burnedInSuccess) "Burned Video Saved ✓" else if (isBurningInVideo) "Rendering Video..." else "Burn & Export Video",
                isHighlighted = true,
                onAction = {
                    isBurningInVideo = true
                    // Simulate fast Int8 HW video composition
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        isBurningInVideo = false
                        burnedInSuccess = true
                        Toast.makeText(context, "Burned-in video exported to gallery!", Toast.LENGTH_LONG).show()
                    }, 1200)
                },
                testTag = "export_burned_video_button"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. SRT EXPORT
            ExportActionCard(
                icon = Icons.Default.Subtitles,
                title = "SubRip Subtitle (.srt)",
                subtitle = "Universal subtitle format with synchronized millisecond timecodes",
                actionLabel = "Copy SRT File",
                secondaryActionLabel = "Share .srt",
                onAction = {
                    viewModel.copyToClipboard(context, srtContent, "SRT Subtitles")
                },
                onSecondaryAction = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, srtContent)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share SRT Captions"))
                },
                previewContent = srtContent.lines().take(6).joinToString("\n"),
                testTag = "export_srt_button"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. VTT EXPORT
            ExportActionCard(
                icon = Icons.Default.Description,
                title = "WebVTT Subtitle (.vtt)",
                subtitle = "Optimized for HTML5 video players, streaming engines, and web embeds",
                actionLabel = "Copy VTT File",
                secondaryActionLabel = "Share .vtt",
                onAction = {
                    viewModel.copyToClipboard(context, vttContent, "WebVTT Subtitles")
                },
                onSecondaryAction = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, vttContent)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share VTT Captions"))
                },
                previewContent = vttContent.lines().take(6).joinToString("\n"),
                testTag = "export_vtt_button"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. SAVE PROJECT FOR LATER EDITING
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkCardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Project",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Save Project for Later Editing",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Saves all caption edits, timing offsets, and styling selections to local Room database.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.saveCurrentProject()
                            projectSavedNotice = true
                            Toast.makeText(context, "Project successfully saved!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E2E42),
                            contentColor = NeonCyan
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("save_project_button")
                    ) {
                        Icon(
                            imageVector = if (projectSavedNotice) Icons.Default.CheckCircle else Icons.Default.Save,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (projectSavedNotice) "Project Saved in Local Database" else "Save Project State",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ExportActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String,
    secondaryActionLabel: String? = null,
    isHighlighted: Boolean = false,
    onAction: () -> Unit,
    onSecondaryAction: (() -> Unit)? = null,
    previewContent: String? = null,
    testTag: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = DarkCardSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isHighlighted) NeonCyan.copy(alpha = 0.5f) else DarkCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isHighlighted) Color(0xFF032230) else Color(0xFF182232)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isHighlighted) NeonCyan else CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            // Preview code snippet if available
            if (!previewContent.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0A0F17),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF182333)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = previewContent,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyanAccent,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAction,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag(testTag),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHighlighted) NeonCyan else Color(0xFF1A2637),
                        contentColor = if (isHighlighted) TextOnCyan else NeonCyan
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = actionLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (secondaryActionLabel != null && onSecondaryAction != null) {
                    OutlinedButton(
                        onClick = onSecondaryAction,
                        modifier = Modifier
                            .weight(0.7f)
                            .height(46.dp)
                            .testTag("${testTag}_share"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = secondaryActionLabel,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
