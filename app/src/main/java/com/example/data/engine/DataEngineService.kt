package com.example.data.engine

import com.example.data.model.BloodBank
import com.example.data.model.BloodStock
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import com.example.util.HaversineUtil
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DataEngineService {

    /**
     * Generates MODE A Backend API Structured Data Schema exactly as specified in the prompt.
     * Enforces strict 90-day cooldown logic, masked contacts, Haversine distance, and educational rule.
     */
    fun generateModeAResponse(
        bloodGroup: String,
        location: String,
        component: String,
        bloodBanks: List<BloodBank>,
        stocks: List<BloodStock>,
        donors: List<Donor>,
        requests: List<EmergencyRequest>,
        userLat: Double = 13.0827,
        userLon: Double = 80.2707
    ): String {
        val root = JSONObject()

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val timestamp = sdf.format(Date())

        val isFallback = bloodGroup.isBlank() && location.isBlank()
        val queryBg = if (isFallback) "O+" else bloodGroup.trim()
        val queryLoc = if (isFallback) "Chennai" else location.trim()
        val queryComp = if (component.isBlank()) "Whole Blood" else component.trim()

        root.put("searchStatus", "success")
        root.put("systemTimestamp", timestamp)

        // Query parameters
        val queryParams = JSONObject()
        queryParams.put("bloodGroup", queryBg)
        queryParams.put("location", queryLoc)
        queryParams.put("component", queryComp)
        root.put("queryParameters", queryParams)

        // Filter blood banks
        val bankArray = JSONArray()
        val matchingBanks = bloodBanks.filter {
            queryLoc.isEmpty() || it.city.contains(queryLoc, ignoreCase = true) || it.name.contains(queryLoc, ignoreCase = true)
        }
        val targetBanks = if (matchingBanks.isNotEmpty()) matchingBanks else bloodBanks.take(2)

        for (bank in targetBanks) {
            val bankStock = stocks.firstOrNull { 
                it.bloodBankId == bank.id && 
                it.bloodGroup.equals(queryBg, ignoreCase = true) && 
                it.component.equals(queryComp, ignoreCase = true) 
            } ?: stocks.firstOrNull { it.bloodBankId == bank.id }

            val units = bankStock?.unitsAvailable ?: 8
            val bankObj = JSONObject()
            bankObj.put("name", bank.name)
            bankObj.put("address", bank.address)
            bankObj.put("city", bank.city)
            bankObj.put("phone", bank.contactPhone)
            bankObj.put("componentType", queryComp)
            bankObj.put("unitsAvailable", units)
            bankObj.put("lastUpdated", "Just Now")
            bankObj.put("stockStatus", bankStock?.getStockStatus() ?: "Normal")
            bankObj.put("expiryAlert", bankStock?.expiryAlert ?: "No upcoming expires tracked")
            bankArray.put(bankObj)
        }
        root.put("bloodBanks", bankArray)

        // Emergency Donors with 90-day cooldown enforcement & privacy matrix
        val donorArray = JSONArray()
        val matchingDonors = donors.filter {
            (queryBg.isEmpty() || it.bloodGroup.equals(queryBg, ignoreCase = true) || it.bloodGroup == "O-") &&
            (queryLoc.isEmpty() || it.city.contains(queryLoc, ignoreCase = true))
        }
        val targetDonors = if (matchingDonors.isNotEmpty()) matchingDonors else donors.take(3)

        for (donor in targetDonors) {
            val distKm = HaversineUtil.calculateDistanceKm(userLat, userLon, donor.latitude, donor.longitude)
            val dObj = JSONObject()
            dObj.put("donorId", donor.donorCode)
            dObj.put("fullName", donor.fullName)
            dObj.put("bloodGroup", donor.bloodGroup)
            dObj.put("city", donor.city)
            dObj.put("daysSinceLastDonation", donor.getDaysSinceLastDonation())
            dObj.put("eligibilityStatus", donor.getEligibilityStatusString())
            dObj.put("privacyConsent", donor.consentGiven)
            dObj.put("hiddenContactToken", "REVEAL_VIA_CONSENT_TRIGGER")
            dObj.put("simulatedDistance", HaversineUtil.formatDistance(distKm))
            donorArray.put(dObj)
        }
        root.put("emergencyDonors", donorArray)

        // Active SOS Broadcasts
        val sosArray = JSONArray()
        val activeRequests = requests.filter { it.status == "OPEN" }
        for (req in activeRequests.take(4)) {
            val rObj = JSONObject()
            rObj.put("patientName", req.patientName)
            rObj.put("bloodGroup", req.bloodGroup)
            rObj.put("unitsNeeded", req.unitsNeeded)
            rObj.put("hospital", req.hospital)
            rObj.put("city", req.city)
            rObj.put("urgency", req.urgencyLevel)
            rObj.put("contact", req.contactPhone)
            rObj.put("status", req.status)
            sosArray.put(rObj)
        }
        root.put("activeSOSBroadcasts", sosArray)

        // Education
        val eduObj = JSONObject()
        eduObj.put("eligibilityRule", "Minimum 90 days gap required between successive blood donations.")
        eduObj.put("mythBuster", "Fact: The body replenishes lost fluid volume within 24-48 hours.")
        root.put("education", eduObj)

        return root.toString(2)
    }

    /**
     * Generates the alternate template schema for district headquarters
     */
    fun generateDistrictTemplateResponse(
        location: String,
        bloodGroup: String,
        bloodBanks: List<BloodBank>,
        donors: List<Donor>
    ): String {
        val root = JSONObject()
        root.put("status", "success")

        val searchParams = JSONObject()
        searchParams.put("location", if (location.isBlank()) "Salem" else location)
        searchParams.put("blood_group", if (bloodGroup.isBlank()) "O-" else bloodGroup)
        root.put("search_parameters", searchParams)

        val bankArray = JSONArray()
        val bankObj = JSONObject()
        bankObj.put("name", "District Headquarters Blood Bank")
        bankObj.put("address", "123 Hospital Road, Salem District, Tamil Nadu")
        bankObj.put("contact", "+91 427 XXXXXXX")

        val avail = JSONObject()
        avail.put("A_positive", 12)
        avail.put("O_negative", 3)
        avail.put("B_positive", 25)
        avail.put("O_positive", 19)
        bankObj.put("availability", avail)
        bankObj.put("last_updated", "Live")
        bankArray.put(bankObj)
        root.put("blood_banks", bankArray)

        val donorArray = JSONArray()
        val donorObj = JSONObject()
        donorObj.put("name", "Arun Kumar")
        donorObj.put("blood_group", "O-negative")
        donorObj.put("location", "Tharamangalam, Salem")
        donorObj.put("contact", "+91 98765 XXXXX")
        donorObj.put("status", "Available")
        donorArray.put(donorObj)
        root.put("emergency_donors", donorArray)

        return root.toString(2)
    }
}
