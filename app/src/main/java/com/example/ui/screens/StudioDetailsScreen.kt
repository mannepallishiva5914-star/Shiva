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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AudioWaveformTrackCard
import com.example.ui.components.GenerateCaptionsCtaButton
import com.example.ui.components.LanguagePickerDialog
import com.example.ui.components.PhoneticTransliterationModeCard
import com.example.ui.components.StitchTopBar
import com.example.ui.components.TransliterationEngineCard
import com.example.ui.components.VideoPreviewCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CaptionViewModel

@Composable
fun StudioDetailsScreen(
    viewModel: CaptionViewModel,
    onNavigateToEditor: () -> Unit,
    onNavigateToStyling: () -> Unit,
    onNavigateToExport: () -> Unit,
    onNavigateToProjects: () -> Unit
) {
    val projectTitle by viewModel.projectTitle.collectAsState()
    val durationMs by viewModel.durationMs.collectAsState()
    val currentPositionMs by viewModel.currentPositionMs.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val confidenceScore by viewModel.confidenceScore.collectAsState()
    val audioChannels by viewModel.audioChannels.collectAsState()
    val sampleRate by viewModel.sampleRate.collectAsState()
    val vocalBandStatus by viewModel.vocalBandStatus.collectAsState()
    val captionStyle by viewModel.captionStyle.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generationProgress by viewModel.generationProgress.collectAsState()
    val generationStatusMessage by viewModel.generationStatusMessage.collectAsState()

    val activeCaption = viewModel.getActiveCaptionAt(currentPositionMs)
    var showLanguagePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            StitchTopBar(
                title = "Export Details",
                onBackClick = onNavigateToProjects,
                onProfileClick = onNavigateToProjects
            )
        },
        bottomBar = {
            GenerateCaptionsCtaButton(
                onClick = {
                    viewModel.generateOfflineCaptions(onComplete = onNavigateToEditor)
                },
                isLoading = isGenerating
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
            // 1. Video Preview Card with live synchronized caption overlay
            VideoPreviewCard(
                currentCaption = activeCaption,
                captionStyle = captionStyle,
                isPlaying = isPlaying,
                currentTimeMs = currentPositionMs,
                durationMs = durationMs,
                onTogglePlay = { viewModel.togglePlayback() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Audio Waveform Track Card
            AudioWaveformTrackCard(
                title = projectTitle,
                durationMs = durationMs,
                currentPositionMs = currentPositionMs,
                sampleRate = sampleRate,
                channels = audioChannels,
                vocalBandStatus = vocalBandStatus,
                onSeek = { viewModel.seekTo(it) }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Workflow Navigation Hub
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickNavTab(
                    icon = Icons.Default.Edit,
                    label = "Edit Captions",
                    onClick = onNavigateToEditor,
                    modifier = Modifier.weight(1f),
                    testTag = "nav_to_editor_tab"
                )
                QuickNavTab(
                    icon = Icons.Default.Palette,
                    label = "Style Controls",
                    onClick = onNavigateToStyling,
                    modifier = Modifier.weight(1f),
                    testTag = "nav_to_styling_tab"
                )
                QuickNavTab(
                    icon = Icons.Default.FileDownload,
                    label = "Export Video",
                    onClick = onNavigateToExport,
                    modifier = Modifier.weight(1f),
                    testTag = "nav_to_export_tab"
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Transliteration Engine Section & Detected Spoken Audio Card
            TransliterationEngineCard(
                language = selectedLanguage,
                confidenceScore = confidenceScore,
                onSwitchLanguageClick = { showLanguagePicker = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Phonetic Transliteration Mode Card with SPEECH SOUND MATRIX
            PhoneticTransliterationModeCard(
                language = selectedLanguage
            )

            // Generation Progress Modal / Indicator if actively processing
            if (isGenerating) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0F1E2C),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            progress = { generationProgress },
                            color = NeonCyan,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Fast-Int8 Neural Engine Processing",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = generationStatusMessage,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = NeonCyan
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showLanguagePicker) {
            LanguagePickerDialog(
                selectedLanguage = selectedLanguage,
                onSelectLanguage = { viewModel.switchLanguage(it) },
                onDismiss = { showLanguagePicker = false }
            )
        }
    }
}

@Composable
private fun QuickNavTab(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = DarkCardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = NeonCyan,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}
