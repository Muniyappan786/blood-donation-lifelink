package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.concurrent.TimeUnit

@Entity(tableName = "blood_banks")
data class BloodBank(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,
    val city: String,
    val state: String,
    val pincode: String,
    val latitude: Double,
    val longitude: Double,
    val contactPhone: String,
    val licenseNumber: String
)

@Entity(
    tableName = "blood_stock",
    indices = [Index(value = ["bloodBankId", "bloodGroup", "component"], unique = true)]
)
data class BloodStock(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bloodBankId: Long,
    val bloodGroup: String, // "O+", "A-", etc.
    val component: String,  // "Whole Blood", "Packed RBC", etc.
    val unitsAvailable: Int,
    val lastUpdatedEpoch: Long = System.currentTimeMillis(),
    val expiryAlert: String = "No upcoming expires tracked"
) {
    fun getStockStatus(): String {
        return when {
            unitsAvailable <= 3 -> "Critical Low"
            unitsAvailable <= 7 -> "Low"
            unitsAvailable <= 20 -> "Normal"
            else -> "Surplus"
        }
    }
}

@Entity(tableName = "donors")
data class Donor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val donorCode: String, // e.g. "LL-7701"
    val fullName: String,
    val email: String,
    val phoneNumber: String, // e.g. "+91 9876543210"
    val bloodGroup: String,
    val city: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val age: Int,
    val weightKg: Double,
    val lastDonationEpoch: Long = 0, // 0 if never donated
    val isAvailable: Boolean = true,
    val chronicIllness: Boolean = false,
    val medication: Boolean = false,
    val recentSurgery: Boolean = false,
    val consentGiven: Boolean = true,
    val verifiedByAdmin: Boolean = true,
    val accountStatus: String = "ACTIVE" // "ACTIVE" or "BLOCKED"
) {
    fun getDaysSinceLastDonation(): Int {
        if (lastDonationEpoch == 0L) return 365 // first time donor
        val diffMs = System.currentTimeMillis() - lastDonationEpoch
        val days = TimeUnit.MILLISECONDS.toDays(diffMs).toInt()
        return if (days < 0) 0 else days
    }

    /**
     * Strict 90-day medical cooldown logic:
     * - Gap between successive donations must be at least 90 days.
     * - Age must be 18..65.
     * - Minimum weight 50.0 kg.
     * - No chronic illness or recent surgery (< 6 months).
     */
    fun isEligible(): Boolean {
        if (accountStatus != "ACTIVE") return false
        if (age !in 18..65) return false
        if (weightKg < 50.0) return false
        if (chronicIllness || recentSurgery) return false
        if (getDaysSinceLastDonation() < 90) return false
        return true
    }

    fun getEligibilityStatusString(): String {
        if (accountStatus == "BLOCKED") return "Blocked by Admin"
        if (chronicIllness) return "Ineligible (Health Flag)"
        if (recentSurgery) return "Ineligible (Recent Surgery)"
        if (age !in 18..65) return "Ineligible (Age Limit 18-65)"
        if (weightKg < 50.0) return "Ineligible (Weight < 50kg)"
        val days = getDaysSinceLastDonation()
        if (days < 90) {
            val remaining = 90 - days
            return "Ineligible (Cooldown: $remaining days left)"
        }
        return "Eligible"
    }

    /**
     * Privacy Matrix: Mask phone number unless revealed (e.g. "+91 98765XXXXX")
     */
    fun getMaskedPhone(): String {
        val clean = phoneNumber.trim()
        return if (clean.length >= 8) {
            clean.substring(0, clean.length - 5) + "XXXXX"
        } else {
            "98765XXXXX"
        }
    }
}

@Entity(tableName = "emergency_requests")
data class EmergencyRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientName: String,
    val bloodGroup: String,
    val component: String,
    val unitsNeeded: Int,
    val hospital: String,
    val city: String,
    val contactPhone: String,
    val urgencyLevel: String, // "CRITICAL", "URGENT", "NORMAL"
    val status: String = "OPEN", // "OPEN", "ACCEPTED", "FULFILLED", "REJECTED"
    val createdAtEpoch: Long = System.currentTimeMillis(),
    val notes: String = "",
    val acceptedDonorCode: String? = null
)

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val actionType: String,
    val description: String,
    val ipAddress: String = "192.168.1.104",
    val timestampEpoch: Long = System.currentTimeMillis()
)
