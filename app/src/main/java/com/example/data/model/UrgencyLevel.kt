package com.example.data.model

enum class UrgencyLevel(val label: String) {
    CRITICAL("Critical - Immediate (< 1 hr)"),
    URGENT("Urgent (< 6 hrs)"),
    NORMAL("Normal (< 24 hrs)");

    companion object {
        fun fromLabel(label: String): UrgencyLevel {
            val sanitized = label.trim().uppercase()
            return entries.firstOrNull { it.name.equals(sanitized, ignoreCase = true) || it.label.startsWith(sanitized, ignoreCase = true) }
                ?: NORMAL
        }
    }
}
