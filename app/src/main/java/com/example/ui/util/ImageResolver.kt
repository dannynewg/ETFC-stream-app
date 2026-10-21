package com.example.ui.util

import com.example.R
import com.example.data.models.Fighter

object ImageResolver {

    /**
     * Resolves an image model (String, Uri, or resource name/ID) into an object Coil can load directly.
     * If it's a local drawable name or android.resource URI, returns the Int resource ID from com.example.R.drawable.
     */
    fun resolve(model: Any?): Any? {
        if (model == null) return null
        if (model is Int) return if (model != 0) model else null
        if (model !is String) return model
        val str = model.trim()
        if (str.isEmpty()) return null

        // If it's a remote URL or file/content URI, return as-is
        if (str.startsWith("http://", ignoreCase = true) ||
            str.startsWith("https://", ignoreCase = true) ||
            str.startsWith("content://", ignoreCase = true) ||
            str.startsWith("file://", ignoreCase = true)
        ) {
            return str
        }

        // Local resource - extract drawable name
        val cleanName = str.substringAfterLast("/").substringBeforeLast(".")
        val resId = getDrawableIdByName(cleanName)
        return resId ?: str
    }

    /**
     * Finds the best photo for a fighter:
     * 1. If fighter.photoUrl is set, try resolving it.
     * 2. If photoUrl is empty or fails to resolve to a valid resource, fallback to profile_<name> or profile_<id>.
     */
    fun getFighterPhoto(fighter: Fighter): Any? {
        if (!fighter.photoUrl.isNullOrBlank()) {
            val direct = resolve(fighter.photoUrl)
            if (direct != null && (direct !is Int || direct != 0)) {
                return direct
            }
        }

        // Fallback by fighter name
        val nameKey = fighter.name.lowercase().trim().replace(" ", "_")
        val idKey = fighter.id.lowercase().removePrefix("f_").trim()

        val byName = getDrawableIdByName("profile_$nameKey")
        if (byName != null) return byName

        val byId = getDrawableIdByName("profile_$idKey")
        if (byId != null) return byId

        return null
    }

    fun getDrawableIdByName(name: String): Int? {
        if (name.isBlank()) return null
        return try {
            val field = R.drawable::class.java.getField(name)
            val resId = field.getInt(null)
            if (resId != 0) resId else null
        } catch (_: Throwable) {
            null
        }
    }
}
