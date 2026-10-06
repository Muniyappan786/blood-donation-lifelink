package com.example.data.model

enum class BloodComponent(val displayName: String) {
    WHOLE_BLOOD("Whole Blood"),
    RBC("Packed RBC"),
    PLATELETS("Platelets"),
    PLASMA("Fresh Frozen Plasma");

    companion object {
        fun fromDisplayName(name: String): BloodComponent {
            val sanitized = name.trim().lowercase()
            return entries.firstOrNull { 
                it.displayName.equals(sanitized, ignoreCase = true) ||
                it.name.equals(sanitized.replace(" ", "_"), ignoreCase = true)
            } ?: WHOLE_BLOOD
        }
    }
}
