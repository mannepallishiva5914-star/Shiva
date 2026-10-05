package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.VideoProjectEntity
import com.example.data.model.CaptionSegment
import com.example.ui.components.StitchTopBar
import com.example.ui.theme.AccentOrange
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectsListScreen(
    viewModel: CaptionViewModel,
    onSelectProject: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val projects by viewModel.allProjects.collectAsState()
    val currentProjectId by viewModel.currentProjectId.collectAsState()

    // Zero-permission Android Photo Picker for Videos
    val pickVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "Imported_Reel.mp4"
            viewModel.importNewVideo(
                title = fileName,
                uri = uri.toString(),
                durationMs = 45000L
            )
            onSelectProject(viewModel.currentProjectId.value)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            StitchTopBar(
                title = "Video Projects",
                onBackClick = onBackClick,
                onProfileClick = {},
                showBack = true
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
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            pickVideoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("import_video_button"),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = TextOnCyan
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Import",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Import Video",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.loadProject("sample_vid_action_2025")
                            onSelectProject("sample_vid_action_2025")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("load_stitch_demo_button"),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Text(
                            text = "Load Stitch Demo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved Captions Library",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "${projects.size} Projects",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyanAccent
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (projects.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCardSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No saved caption projects yet",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Import a video or load the Stitch demo project to start generating synchronized captions.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(projects, key = { it.id }) { proj ->
                        val isSelected = proj.id == currentProjectId
                        ProjectCardItem(
                            project = proj,
                            isSelected = isSelected,
                            onSelect = {
                                viewModel.loadProject(proj.id)
                                onSelectProject(proj.id)
                            },
                            onDelete = {
                                // Don't delete if only one
                                if (projects.size > 1) {
                                    // Room deletion
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectCardItem(
    project: VideoProjectEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(project.updatedAt) {
        SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(project.updatedAt))
    }
    val captionCount = remember(project.captionsJson) {
        CaptionSegment.parseList(project.captionsJson).size
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onSelect)
            .testTag("project_item_${project.id}"),
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) Color(0xFF142232) else DarkCardSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) NeonCyan else DarkCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Color(0xFF072F40) else Color(0xFF16202E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = null,
                    tint = if (isSelected) NeonCyan else CyanAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = project.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ConfidenceGreen)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${project.detectedLanguage} • $captionCount segments",
                    fontSize = 11.sp,
                    color = CyanAccent
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Length: ${CaptionSegment.formatMs(project.durationMs)} • $dateStr",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted
                )
            }
        }
    }
}
