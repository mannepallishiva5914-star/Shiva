package com.example.data.repository

import com.example.data.db.ProjectDao
import com.example.data.db.VideoProjectEntity
import com.example.data.model.CaptionBgStyle
import com.example.data.model.CaptionFont
import com.example.data.model.CaptionSegment
import com.example.data.model.CaptionStyle
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CaptionProjectRepository(private val projectDao: ProjectDao) {

    val allProjects: Flow<List<VideoProjectEntity>> = projectDao.getAllProjects()

    suspend fun getProject(id: String): VideoProjectEntity? {
        return projectDao.getProjectById(id)
    }

    suspend fun saveProject(project: VideoProjectEntity) {
        projectDao.insertOrUpdate(project)
    }

    suspend fun deleteProject(id: String) {
        projectDao.deleteById(id)
    }

    suspend fun initializeDefaultSampleIfEmpty() {
        val sampleId = "sample_vid_action_2025"
        val existing = projectDao.getProjectById(sampleId)
        if (existing == null) {
            val sampleCaptions = listOf(
                CaptionSegment(
                    id = "seg_1",
                    startMs = 1200L,
                    endMs = 4500L,
                    phoneticText = "Evaraina edhurithe...",
                    originalScriptText = "ఎవరైనా ఎదురితే...",
                    confidence = 0.985f
                ),
                CaptionSegment(
                    id = "seg_2",
                    startMs = 5100L,
                    endMs = 9800L,
                    phoneticText = "Thaggedhele...",
                    originalScriptText = "తగ్గేదే లే...",
                    confidence = 0.992f
                ),
                CaptionSegment(
                    id = "seg_3",
                    startMs = 10400L,
                    endMs = 15200L,
                    phoneticText = "Asalu mundhuki raaniyyara",
                    originalScriptText = "అసలు ముందుకి రానియ్యరా",
                    confidence = 0.978f
                ),
                CaptionSegment(
                    id = "seg_4",
                    startMs = 16000L,
                    endMs = 21500L,
                    phoneticText = "Choodu bhayya entha dhairyam",
                    originalScriptText = "చూడు భయ్యా ఎంత ధైర్యం",
                    confidence = 0.981f
                ),
                CaptionSegment(
                    id = "seg_5",
                    startMs = 22200L,
                    endMs = 28900L,
                    phoneticText = "Gattiga cheppandi andhariki",
                    originalScriptText = "గట్టిగా చెప్పండి అందరికి",
                    confidence = 0.988f
                ),
                CaptionSegment(
                    id = "seg_6",
                    startMs = 29500L,
                    endMs = 36000L,
                    phoneticText = "Thaggedhele bhai, thaggedhele!",
                    originalScriptText = "తగ్గేదే లే భాయ్, తగ్గేదే లే!",
                    confidence = 0.995f
                ),
                CaptionSegment(
                    id = "seg_7",
                    startMs = 37200L,
                    endMs = 46800L,
                    phoneticText = "Okka adugu kooda venakki veyyanu",
                    originalScriptText = "ఒక్క అడుగు కూడా వెనక్కి వేయను",
                    confidence = 0.979f
                )
            )

            val sampleStyle = CaptionStyle(
                fontChoice = CaptionFont.BOLD_IMPACT,
                fontSizeSp = 24,
                textColorHex = 0xFFFFFFFF,
                bgStyle = CaptionBgStyle.TRANSLUCENT,
                positionRatioY = 0.82f
            )

            val defaultSample = VideoProjectEntity(
                id = sampleId,
                title = "VID_20250512_ACTION.mp4",
                videoUri = null,
                durationMs = 48200L,
                detectedLanguage = "Telugu (తెలుగు)",
                languageCode = "te",
                confidenceScore = 0.984f,
                audioChannels = "2-CH PCM",
                sampleRate = "48.0 kHz",
                vocalBandStatus = "CLEAN VOCAL BAND",
                captionsJson = CaptionSegment.serializeList(sampleCaptions),
                styleJson = sampleStyle.toJson(),
                updatedAt = System.currentTimeMillis()
            )
            projectDao.insertOrUpdate(defaultSample)
        }
    }

    suspend fun createNewProject(
        title: String,
        videoUri: String?,
        detectedLang: String = "Telugu (తెలుగు)",
        langCode: String = "te",
        durationMs: Long = 45000L
    ): VideoProjectEntity {
        val newId = UUID.randomUUID().toString()
        val defaultCaptions = listOf(
            CaptionSegment(
                id = UUID.randomUUID().toString(),
                startMs = 1000L,
                endMs = 4000L,
                phoneticText = "Ready to roll...",
                originalScriptText = "Ready to roll...",
                confidence = 0.99f
            )
        )
        val project = VideoProjectEntity(
            id = newId,
            title = title,
            videoUri = videoUri,
            durationMs = durationMs,
            detectedLanguage = detectedLang,
            languageCode = langCode,
            confidenceScore = 0.978f,
            audioChannels = "2-CH PCM",
            sampleRate = "48.0 kHz",
            vocalBandStatus = "CLEAN VOCAL BAND",
            captionsJson = CaptionSegment.serializeList(defaultCaptions),
            styleJson = CaptionStyle().toJson(),
            updatedAt = System.currentTimeMillis()
        )
        projectDao.insertOrUpdate(project)
        return project
    }
}
