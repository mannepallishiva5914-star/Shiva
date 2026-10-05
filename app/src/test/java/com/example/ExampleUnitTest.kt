package com.example

import com.example.data.model.CaptionBgStyle
import com.example.data.model.CaptionFont
import com.example.data.model.CaptionSegment
import com.example.data.model.CaptionStyle
import com.example.engine.TransliterationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TransliterationEngineUnitTest {

    @Test
    fun testTeluguPhoneticNotTranslation() {
        val teLang = TransliterationEngine.getLanguageByCode("te")
        assertEquals("Telugu", teLang.name)
        assertEquals("Thaggedhele", teLang.sampleOutputScript)
        // Verify output is NOT the English translated meaning
        assertFalse(teLang.sampleOutputScript.contains("yield", ignoreCase = true))
        assertFalse(teLang.sampleOutputScript.contains("leave", ignoreCase = true))

        val captions = TransliterationEngine.generateAcousticCaptions("te", 48000L)
        assertTrue(captions.isNotEmpty())
        val thaggedheleSeg = captions.firstOrNull { it.phoneticText.contains("Thaggedhele") }
        assertNotNull(thaggedheleSeg)
    }

    @Test
    fun testEnglishVerbatimHighAccuracy() {
        val enLang = TransliterationEngine.getLanguageByCode("en")
        assertEquals("English", enLang.name)
        assertTrue(enLang.defaultConfidence >= 0.99f)

        val captions = TransliterationEngine.generateAcousticCaptions("en", 30000L)
        assertTrue(captions.isNotEmpty())
        assertEquals(captions[0].phoneticText, "Check the perimeter immediately")
    }

    @Test
    fun testSegmentRegeneration() {
        val seg = CaptionSegment(
            startMs = 5000L,
            endMs = 9000L,
            phoneticText = "Thaggedhele...",
            originalScriptText = "తగ్గేదే లే..."
        )
        val regenerated = TransliterationEngine.regenerateSegment(seg, "te")
        assertNotNull(regenerated)
        assertTrue(regenerated.phoneticText.isNotBlank())
        assertEquals(seg.startMs, regenerated.startMs)
    }

    @Test
    fun testSrtAndVttExport() {
        val list = listOf(
            CaptionSegment(startMs = 1000L, endMs = 3500L, phoneticText = "Hello world"),
            CaptionSegment(startMs = 4000L, endMs = 7200L, phoneticText = "Thaggedhele")
        )

        val srt = TransliterationEngine.exportToSrt(list)
        assertTrue(srt.contains("00:00:01,000 --> 00:00:03,500"))
        assertTrue(srt.contains("Hello world"))
        assertTrue(srt.contains("Thaggedhele"))

        val vtt = TransliterationEngine.exportToVtt(list)
        assertTrue(vtt.startsWith("WEBVTT"))
        assertTrue(vtt.contains("00:00:01.000 --> 00:00:03.500"))
    }

    @Test
    fun testCaptionStyleSerialization() {
        val style = CaptionStyle(
            fontChoice = CaptionFont.BOLD_IMPACT,
            fontSizeSp = 24,
            textColorHex = 0xFF00F2FE,
            bgStyle = CaptionBgStyle.NEON_GLOW,
            positionRatioY = 0.82f
        )
        val json = style.toJson()
        val restored = CaptionStyle.fromJson(json)

        assertEquals(CaptionFont.BOLD_IMPACT, restored.fontChoice)
        assertEquals(24, restored.fontSizeSp)
        assertEquals(0xFF00F2FE, restored.textColorHex)
        assertEquals(CaptionBgStyle.NEON_GLOW, restored.bgStyle)
        assertEquals(0.82f, restored.positionRatioY, 0.01f)
    }
}
