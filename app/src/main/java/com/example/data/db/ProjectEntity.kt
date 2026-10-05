package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_projects")
data class VideoProjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val videoUri: String?,
    val durationMs: Long,
    val detectedLanguage: String,
    val languageCode: String,
    val confidenceScore: Float,
    val audioChannels: String,
    val sampleRate: String,
    val vocalBandStatus: String,
    val captionsJson: String,
    val styleJson: String,
    val updatedAt: Long = System.currentTimeMillis()
)
