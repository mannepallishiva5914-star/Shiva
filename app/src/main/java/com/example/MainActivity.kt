package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CaptionEditorScreen
import com.example.ui.screens.CaptionStylingScreen
import com.example.ui.screens.ExportScreen
import com.example.ui.screens.ProjectsListScreen
import com.example.ui.screens.StudioDetailsScreen
import com.example.ui.theme.PhonoSubTheme
import com.example.ui.viewmodel.CaptionViewModel

enum class AppScreen {
    STUDIO_DETAILS, // The primary screen matching Stitch reference design
    CAPTION_EDITOR, // Interactive video preview & timeline caption editor
    CAPTION_STYLING, // 5 font choices, size, color, background, position
    EXPORT_SCREEN, // SRT, VTT, burned-in video export & save
    PROJECTS_LIST // Local projects library & video picker
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhonoSubTheme {
                val viewModel: CaptionViewModel = viewModel()
                MainAppNavHost(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppNavHost(viewModel: CaptionViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.STUDIO_DETAILS) }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "AppScreenTransition",
        modifier = Modifier.fillMaxSize()
    ) { screen ->
        when (screen) {
            AppScreen.STUDIO_DETAILS -> {
                StudioDetailsScreen(
                    viewModel = viewModel,
                    onNavigateToEditor = { currentScreen = AppScreen.CAPTION_EDITOR },
                    onNavigateToStyling = { currentScreen = AppScreen.CAPTION_STYLING },
                    onNavigateToExport = { currentScreen = AppScreen.EXPORT_SCREEN },
                    onNavigateToProjects = { currentScreen = AppScreen.PROJECTS_LIST }
                )
            }

            AppScreen.CAPTION_EDITOR -> {
                BackHandler { currentScreen = AppScreen.STUDIO_DETAILS }
                CaptionEditorScreen(
                    viewModel = viewModel,
                    onBackClick = { currentScreen = AppScreen.STUDIO_DETAILS },
                    onNavigateToStyling = { currentScreen = AppScreen.CAPTION_STYLING },
                    onNavigateToExport = { currentScreen = AppScreen.EXPORT_SCREEN }
                )
            }

            AppScreen.CAPTION_STYLING -> {
                BackHandler { currentScreen = AppScreen.CAPTION_EDITOR }
                CaptionStylingScreen(
                    viewModel = viewModel,
                    onBackClick = { currentScreen = AppScreen.CAPTION_EDITOR },
                    onNavigateToExport = { currentScreen = AppScreen.EXPORT_SCREEN }
                )
            }

            AppScreen.EXPORT_SCREEN -> {
                BackHandler { currentScreen = AppScreen.STUDIO_DETAILS }
                ExportScreen(
                    viewModel = viewModel,
                    onBackClick = { currentScreen = AppScreen.STUDIO_DETAILS },
                    onNavigateToProjects = { currentScreen = AppScreen.PROJECTS_LIST }
                )
            }

            AppScreen.PROJECTS_LIST -> {
                BackHandler { currentScreen = AppScreen.STUDIO_DETAILS }
                ProjectsListScreen(
                    viewModel = viewModel,
                    onSelectProject = {
                        currentScreen = AppScreen.STUDIO_DETAILS
                    },
                    onBackClick = { currentScreen = AppScreen.STUDIO_DETAILS }
                )
            }
        }
    }
}
