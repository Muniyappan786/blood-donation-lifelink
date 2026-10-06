package com.example.data.repository

import com.example.data.local.BloodDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.BloodBank
import com.example.data.model.BloodStock
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import kotlinx.coroutines.flow.Flow

class BloodRepository(private val database: BloodDatabase) {

    val allBloodBanks: Flow<List<BloodBank>> = database.bloodBankDao().getAllBloodBanks()
    val allStocks: Flow<List<BloodStock>> = database.bloodStockDao().getAllStocks()
    val allDonors: Flow<List<Donor>> = database.donorDao().getAllDonors()
    val openRequests: Flow<List<EmergencyRequest>> = database.emergencyRequestDao().getOpenRequests()
    val allRequests: Flow<List<EmergencyRequest>> = database.emergencyRequestDao().getAllRequests()
    val recentActivityLogs: Flow<List<ActivityLog>> = database.activityLogDao().getRecentLogs()

    suspend fun createEmergencyRequest(request: EmergencyRequest): Long {
        val id = database.emergencyRequestDao().insertRequest(request)
        database.activityLogDao().insertLog(
            ActivityLog(
                actionType = "SOS_BROADCAST",
                description = "Emergency SOS created for ${request.patientName} (${request.bloodGroup}, ${request.unitsNeeded} units) at ${request.hospital}, ${request.city} [${request.urgencyLevel}]",
                ipAddress = "192.168.1.18"
            )
        )
        return id
    }

    suspend fun registerDonor(donor: Donor): Long {
        val id = database.donorDao().insertDonor(donor)
        database.activityLogDao().insertLog(
            ActivityLog(
                actionType = "DONOR_REGISTRATION",
                description = "New donor registered: ${donor.fullName} (${donor.bloodGroup}) in ${donor.city}. Cooldown: ${donor.getDaysSinceLastDonation()}d. Status: ${donor.getEligibilityStatusString()}",
                ipAddress = "192.168.1.22"
            )
        )
        return id
    }

    suspend fun acceptEmergencyAlert(requestId: Long, donorCode: String, patientName: String) {
        database.emergencyRequestDao().updateStatus(requestId, "ACCEPTED", donorCode)
        database.activityLogDao().insertLog(
            ActivityLog(
                actionType = "DONATION_ACCEPTED",
                description = "Donor $donorCode accepted SOS request for patient $patientName. Contact details unmasked.",
                ipAddress = "192.168.1.50"
            )
        )
    }

    suspend fun fulfillEmergencyRequest(requestId: Long, patientName: String) {
        database.emergencyRequestDao().updateStatus(requestId, "FULFILLED", null)
        database.activityLogDao().insertLog(
            ActivityLog(
                actionType = "REQUEST_FULFILLED",
                description = "Emergency request for $patientName marked as fulfilled.",
                ipAddress = "192.168.1.50"
            )
        )
    }

    suspend fun adjustStockUnits(bankId: Long, bankName: String, bloodGroup: String, component: String, delta: Int) {
        val now = System.currentTimeMillis()
        database.bloodStockDao().adjustUnits(bankId, bloodGroup, component, delta, now)
        val verb = if (delta >= 0) "+$delta units added" else "$delta units allocated"
        database.activityLogDao().insertLog(
            ActivityLog(
                actionType = "STOCK_ADJUSTMENT",
                description = "Admin stock adjustment at $bankName: $bloodGroup ($component) -> $verb.",
                ipAddress = "192.168.1.100"
            )
        )
    }

    suspend fun toggleDonorStatus(donor: Donor) {
        val newStatus = if (donor.accountStatus == "ACTIVE") "BLOCKED" else "ACTIVE"
        database.donorDao().updateAccountStatus(donor.id, newStatus)
        database.activityLogDao().insertLog(
            ActivityLog(
                actionType = "USER_STATUS_CHANGE",
                description = "Admin modified account status for ${donor.fullName} ($newStatus).",
                ipAddress = "192.168.1.100"
            )
        )
    }

    suspend fun toggleDonorVerification(donor: Donor) {
        val newVerification = !donor.verifiedByAdmin
        database.donorDao().updateVerification(donor.id, newVerification)
        val status = if (newVerification) "VERIFIED" else "UNVERIFIED"
        database.activityLogDao().insertLog(
            ActivityLog(
                actionType = "ADMIN_VERIFICATION",
                description = "Admin marked donor ${donor.fullName} as $status.",
                ipAddress = "192.168.1.100"
            )
        )
    }
}
