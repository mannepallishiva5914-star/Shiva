package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class CaptionSegment(
    val id: String = UUID.randomUUID().toString(),
    var startMs: Long,
    var endMs: Long,
    var phoneticText: String,
    var originalScriptText: String = "",
    var confidence: Float = 0.98f
) {
    fun formattedStartTime(): String = formatMs(startMs)
    fun formattedEndTime(): String = formatMs(endMs)

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("startMs", startMs)
            put("endMs", endMs)
            put("phoneticText", phoneticText)
            put("originalScriptText", originalScriptText)
            put("confidence", confidence.toDouble())
        }
    }

    companion object {
        fun fromJson(json: JSONObject): CaptionSegment {
            return CaptionSegment(
                id = json.optString("id", UUID.randomUUID().toString()),
                startMs = json.optLong("startMs", 0L),
                endMs = json.optLong("endMs", 1000L),
                phoneticText = json.optString("phoneticText", ""),
                originalScriptText = json.optString("originalScriptText", ""),
                confidence = json.optDouble("confidence", 0.95).toFloat()
            )
        }

        fun parseList(jsonString: String): List<CaptionSegment> {
            val list = mutableListOf<CaptionSegment>()
            try {
                val array = JSONArray(jsonString)
                for (i in 0 until array.length()) {
                    list.add(fromJson(array.getJSONObject(i)))
                }
            } catch (e: Exception) {
                // Return empty if parsing failed
            }
            return list
        }

        fun serializeList(list: List<CaptionSegment>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }

        fun formatMs(ms: Long): String {
            val totalSeconds = ms / 1000
            val millis = (ms % 1000) / 10
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d.%02d", minutes, seconds, millis)
        }

        fun formatSrtTime(ms: Long): String {
            val totalSeconds = ms / 1000
            val millis = ms % 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, millis)
        }

        fun formatVttTime(ms: Long): String {
            val totalSeconds = ms / 1000
            val millis = ms % 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis)
        }
    }
}

enum class CaptionFont(val id: Int, val displayName: String, val description: String) {
    CINEMATIC_SANS(0, "Cinematic Sans", "Modern clean Sans-Serif for maximum readability"),
    BOLD_IMPACT(1, "Bold Impact", "Heavy punchy letters for high-energy Reels & Shorts"),
    SUB_MONOSPACE(2, "Sub Monospace", "Precision tech acoustic font with monospace width"),
    EDITORIAL_SERIF(3, "Editorial Serif", "Refined cinematic elegance with sharp serifs"),
    REELS_NEON(4, "Reels Pop", "Trendy rounded modern aesthetic for social video")
}

enum class CaptionBgStyle(val id: String, val displayName: String) {
    NONE("NONE", "None (Clean)"),
    TRANSLUCENT("TRANSLUCENT", "Soft Backdrop"),
    SOLID("SOLID", "Solid Charcoal"),
    OUTLINE("OUTLINE", "High Contrast Outline"),
    NEON_GLOW("NEON_GLOW", "Cyan Neon Glow")
}

data class CaptionStyle(
    val fontChoice: CaptionFont = CaptionFont.BOLD_IMPACT,
    val fontSizeSp: Int = 22,
    val textColorHex: Long = 0xFFFFFFFF,
    val bgStyle: CaptionBgStyle = CaptionBgStyle.TRANSLUCENT,
    val positionRatioY: Float = 0.82f // 0.15 = top, 0.50 = center, 0.82 = bottom
) {
    fun toJson(): String {
        return JSONObject().apply {
            put("fontChoice", fontChoice.name)
            put("fontSizeSp", fontSizeSp)
            put("textColorHex", textColorHex)
            put("bgStyle", bgStyle.name)
            put("positionRatioY", positionRatioY.toDouble())
        }.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): CaptionStyle {
            return try {
                val obj = JSONObject(jsonStr)
                CaptionStyle(
                    fontChoice = CaptionFont.valueOf(obj.optString("fontChoice", CaptionFont.BOLD_IMPACT.name)),
                    fontSizeSp = obj.optInt("fontSizeSp", 22),
                    textColorHex = obj.optLong("textColorHex", 0xFFFFFFFF),
                    bgStyle = CaptionBgStyle.valueOf(obj.optString("bgStyle", CaptionBgStyle.TRANSLUCENT.name)),
                    positionRatioY = obj.optDouble("positionRatioY", 0.82).toFloat()
                )
            } catch (e: Exception) {
                CaptionStyle()
            }
        }
    }
}
