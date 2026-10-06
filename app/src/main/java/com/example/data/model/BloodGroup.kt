package com.example.data.model

enum class BloodGroup(val label: String) {
    O_POS("O+"),
    O_NEG("O-"),
    A_POS("A+"),
    A_NEG("A-"),
    B_POS("B+"),
    B_NEG("B-"),
    AB_POS("AB+"),
    AB_NEG("AB-");

    companion object {
        fun fromLabel(label: String): BloodGroup {
            val sanitized = label.trim().uppercase()
            return entries.firstOrNull { 
                it.label.equals(sanitized, ignoreCase = true) || 
                it.name.equals(sanitized.replace("+", "_POS").replace("-", "_NEG"), ignoreCase = true)
            } ?: O_POS
        }
    }

    /**
     * Compatibility check for red blood cell / whole blood transfusion
     */
    fun canDonateTo(recipient: BloodGroup): Boolean {
        return when (this) {
            O_NEG -> true // Universal RBC donor
            O_POS -> recipient in listOf(O_POS, A_POS, B_POS, AB_POS)
            A_NEG -> recipient in listOf(A_NEG, A_POS, AB_NEG, AB_POS)
            A_POS -> recipient in listOf(A_POS, AB_POS)
            B_NEG -> recipient in listOf(B_NEG, B_POS, AB_NEG, AB_POS)
            B_POS -> recipient in listOf(B_POS, AB_POS)
            AB_NEG -> recipient in listOf(AB_NEG, AB_POS)
            AB_POS -> recipient == AB_POS // Only AB+
        }
    }

    fun canReceiveFrom(donor: BloodGroup): Boolean {
        return donor.canDonateTo(this)
    }
}
