package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CaptionSegment
import com.example.ui.components.AudioWaveformTrackCard
import com.example.ui.components.StitchTopBar
import com.example.ui.components.VideoPreviewCard
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.ConfidenceGreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSubtleBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextOnCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningRed
import com.example.ui.viewmodel.CaptionViewModel

@Composable
fun CaptionEditorScreen(
    viewModel: CaptionViewModel,
    onBackClick: () -> Unit,
    onNavigateToStyling: () -> Unit,
    onNavigateToExport: () -> Unit
) {
    val projectTitle by viewModel.projectTitle.collectAsState()
    val durationMs by viewModel.durationMs.collectAsState()
    val currentPositionMs by viewModel.currentPositionMs.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val captions by viewModel.captions.collectAsState()
    val captionStyle by viewModel.captionStyle.collectAsState()
    val audioChannels by viewModel.audioChannels.collectAsState()
    val sampleRate by viewModel.sampleRate.collectAsState()
    val vocalBandStatus by viewModel.vocalBandStatus.collectAsState()

    val activeCaption = viewModel.getActiveCaptionAt(currentPositionMs)
    var editingSegment by remember { mutableStateOf<CaptionSegment?>(null) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            StitchTopBar(
                title = "Timeline Editor",
                onBackClick = onBackClick,
                onProfileClick = onBackClick
            )
        },
        bottomBar = {
            // Bottom Action Bar: Add Caption + Style & Export quick jump
            Surface(
                color = DarkCardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.addCaptionAtCurrentPosition() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("add_caption_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NeonCyan
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add at Playhead", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onNavigateToStyling,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("go_to_styling_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = TextOnCyan
                        )
                    ) {
                        Text("Style Captions", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Video Preview
            VideoPreviewCard(
                currentCaption = activeCaption,
                captionStyle = captionStyle,
                isPlaying = isPlaying,
                currentTimeMs = currentPositionMs,
                durationMs = durationMs,
                onTogglePlay = { viewModel.togglePlayback() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Playback Transport Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.jumpRelative(-3000L) },
                    modifier = Modifier.testTag("rewind_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Rewind 3s",
                        tint = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                        .clickable { viewModel.togglePlayback() }
                        .testTag("editor_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = TextOnCyan,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { viewModel.jumpRelative(3000L) },
                    modifier = Modifier.testTag("forward_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Forward 3s",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Audio Waveform Scrubber
            AudioWaveformTrackCard(
                title = projectTitle,
                durationMs = durationMs,
                currentPositionMs = currentPositionMs,
                sampleRate = sampleRate,
                channels = audioChannels,
                vocalBandStatus = vocalBandStatus,
                onSeek = { viewModel.seekTo(it) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Captions List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Captions Track (${captions.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Tap to jump playhead",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Synchronized Caption List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(captions, key = { _, item -> item.id }) { index, seg ->
                    val isActive = currentPositionMs in seg.startMs..seg.endMs
                    CaptionRowCard(
                        index = index + 1,
                        segment = seg,
                        isActive = isActive,
                        onSeekToSegment = { viewModel.seekTo(seg.startMs) },
                        onEdit = { editingSegment = seg },
                        onRegenerate = { viewModel.regenerateCaption(seg.id) },
                        onDelete = { viewModel.deleteCaption(seg.id) }
                    )
                }
            }
        }

        // Segment Edit Dialog
        editingSegment?.let { seg ->
            CaptionEditDialog(
                segment = seg,
                onDismiss = { editingSegment = null },
                onSave = { updatedText, startMs, endMs ->
                    viewModel.updateCaptionText(seg.id, updatedText)
                    viewModel.updateCaptionTiming(seg.id, startMs, endMs)
                    editingSegment = null
                }
            )
        }
    }
}

@Composable
private fun CaptionRowCard(
    index: Int,
    segment: CaptionSegment,
    isActive: Boolean,
    onSeekToSegment: () -> Unit,
    onEdit: () -> Unit,
    onRegenerate: () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor = if (isActive) NeonCyan else DarkCardBorder
    val bgColor = if (isActive) Color(0xFF142538) else DarkCardSurface

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onSeekToSegment)
            .testTag("caption_segment_${segment.id}"),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Segment #, Timing Range, and Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isActive) NeonCyan else Color(0xFF1C2739)
                    ) {
                        Text(
                            text = "#$index",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) TextOnCyan else TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${segment.formattedStartTime()} → ${segment.formattedEndTime()}",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (isActive) AccentOrange else TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Regenerate Button
                    IconButton(
                        onClick = onRegenerate,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("regenerate_btn_${segment.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Regenerate pronunciation",
                            tint = CyanAccent,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Edit Button
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("edit_btn_${segment.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit caption",
                            tint = TextPrimary,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Delete Button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_btn_${segment.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete caption",
                            tint = WarningRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Phonetic Caption Text
            Text(
                text = segment.phoneticText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) NeonCyan else TextPrimary
            )

            if (segment.originalScriptText.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Acoustic Source: ${segment.originalScriptText}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CaptionEditDialog(
    segment: CaptionSegment,
    onDismiss: () -> Unit,
    onSave: (String, Long, Long) -> Unit
) {
    var textValue by remember { mutableStateOf(segment.phoneticText) }
    var startMsValue by remember { mutableStateOf(segment.startMs.toString()) }
    var endMsValue by remember { mutableStateOf(segment.endMs.toString()) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkCardSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Edit Caption Segment",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Caption Text Field
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = { Text("Phonetic Spoken Words") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_caption_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Start Time & End Time in ms
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = startMsValue,
                        onValueChange = { startMsValue = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Start (ms)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = endMsValue,
                        onValueChange = { endMsValue = it.filter { ch -> ch.isDigit() } },
                        label = { Text("End (ms)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Dialog Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val start = startMsValue.toLongOrNull() ?: segment.startMs
                            val end = endMsValue.toLongOrNull() ?: segment.endMs
                            onSave(textValue, start, end)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = TextOnCyan
                        ),
                        modifier = Modifier.testTag("save_caption_button")
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
