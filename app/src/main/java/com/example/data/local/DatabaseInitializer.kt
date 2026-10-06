package com.example.data.local

import com.example.data.model.ActivityLog
import com.example.data.model.BloodBank
import com.example.data.model.BloodComponent
import com.example.data.model.BloodGroup
import com.example.data.model.BloodStock
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

object DatabaseInitializer {

    suspend fun populateIfEmpty(database: BloodDatabase) = withContext(Dispatchers.IO) {
        val bankCount = database.bloodBankDao().getCount()
        if (bankCount > 0) return@withContext

        // 1. Seed Blood Banks
        val banks = listOf(
            BloodBank(
                id = 1,
                name = "District Headquarters Blood Bank",
                address = "123 Hospital Road, Salem District",
                city = "Salem",
                state = "Tamil Nadu",
                pincode = "636001",
                latitude = 11.6643,
                longitude = 78.1460,
                contactPhone = "+91 427 2415500",
                licenseNumber = "TN-SLM-BB-091"
            ),
            BloodBank(
                id = 2,
                name = "Central Red Cross Hub",
                address = "12 Medical Road, Egmore",
                city = "Chennai",
                state = "Tamil Nadu",
                pincode = "600008",
                latitude = 13.0827,
                longitude = 80.2707,
                contactPhone = "+91 98765 43210",
                licenseNumber = "TN-CHN-BB-102"
            ),
            BloodBank(
                id = 3,
                name = "Apollo General Blood Center",
                address = "Greams Road, Thousand Lights",
                city = "Chennai",
                state = "Tamil Nadu",
                pincode = "600006",
                latitude = 13.0604,
                longitude = 80.2496,
                contactPhone = "+91 44 2829 0200",
                licenseNumber = "TN-CHN-BB-204"
            ),
            BloodBank(
                id = 4,
                name = "Government Medical College Blood Bank",
                address = "Medical College Road",
                city = "Coimbatore",
                state = "Tamil Nadu",
                pincode = "641018",
                latitude = 11.0168,
                longitude = 76.9558,
                contactPhone = "+91 422 2301393",
                licenseNumber = "TN-CBE-BB-305"
            ),
            BloodBank(
                id = 5,
                name = "Victoria Memorial Blood Bank",
                address = "Fort Area, City Market",
                city = "Bengaluru",
                state = "Karnataka",
                pincode = "560002",
                latitude = 12.9620,
                longitude = 77.5750,
                contactPhone = "+91 80 2670 1150",
                licenseNumber = "KA-BLR-BB-410"
            )
        )
        database.bloodBankDao().insertAll(banks)

        // 2. Seed Blood Stocks for banks
        val now = System.currentTimeMillis()
        val stocks = mutableListOf<BloodStock>()
        val components = listOf("Whole Blood", "Packed RBC", "Platelets", "Fresh Frozen Plasma")
        val groups = BloodGroup.entries.map { it.label }

        for (b in banks) {
            for (g in groups) {
                for (comp in components) {
                    val defaultUnits = when {
                        g == "O-" -> (2..5).random() // rare
                        g == "AB-" -> (1..4).random() // rare
                        comp == "Platelets" -> (4..12).random()
                        g in listOf("O+", "B+", "A+") -> (10..24).random()
                        else -> (5..15).random()
                    }
                    stocks.add(
                        BloodStock(
                            bloodBankId = b.id,
                            bloodGroup = g,
                            component = comp,
                            unitsAvailable = defaultUnits,
                            lastUpdatedEpoch = now - (0..12).random() * 3600000L,
                            expiryAlert = if (defaultUnits <= 3) "Urgent shortage alert!" else "No upcoming expires tracked"
                        )
                    )
                }
            }
        }
        database.bloodStockDao().insertAll(stocks)

        // 3. Seed Emergency Donors (reflecting both eligible and 90-day cooldown states)
        val dayMs = TimeUnit.DAYS.toMillis(1)
        val donors = listOf(
            Donor(
                id = 1,
                donorCode = "LL-7701",
                fullName = "Arun Kumar",
                email = "arun.kumar@gmail.com",
                phoneNumber = "+91 98765 12091",
                bloodGroup = "O-",
                city = "Salem",
                address = "Tharamangalam, Salem",
                latitude = 11.6980,
                longitude = 77.9780,
                age = 29,
                weightKg = 68.0,
                lastDonationEpoch = now - (120 * dayMs), // 120 days ago -> ELIGIBLE
                isAvailable = true,
                consentGiven = true,
                verifiedByAdmin = true
            ),
            Donor(
                id = 2,
                donorCode = "LL-7702",
                fullName = "Priya Sundaram",
                email = "priya.s@gmail.com",
                phoneNumber = "+91 98401 77345",
                bloodGroup = "O+",
                city = "Chennai",
                address = "Anna Nagar, Chennai",
                latitude = 13.0850,
                longitude = 80.2100,
                age = 27,
                weightKg = 54.5,
                lastDonationEpoch = now - (140 * dayMs), // 140 days -> ELIGIBLE
                isAvailable = true,
                consentGiven = true,
                verifiedByAdmin = true
            ),
            Donor(
                id = 3,
                donorCode = "LL-7703",
                fullName = "Karthik Raja",
                email = "karthik.raja@outlook.com",
                phoneNumber = "+91 98765 44321",
                bloodGroup = "A+",
                city = "Salem",
                address = "Alagapuram, Salem",
                latitude = 11.6780,
                longitude = 78.1350,
                age = 31,
                weightKg = 72.0,
                lastDonationEpoch = now - (35 * dayMs), // 35 days -> INELIGIBLE (COOLDOWN)
                isAvailable = false,
                consentGiven = true,
                verifiedByAdmin = true
            ),
            Donor(
                id = 4,
                donorCode = "LL-7704",
                fullName = "Meera Nair",
                email = "meera.nair@hospital.org",
                phoneNumber = "+91 94470 88219",
                bloodGroup = "B+",
                city = "Coimbatore",
                address = "Peelamedu, Coimbatore",
                latitude = 11.0250,
                longitude = 77.0120,
                age = 26,
                weightKg = 58.0,
                lastDonationEpoch = now - (110 * dayMs), // 110 days -> ELIGIBLE
                isAvailable = true,
                consentGiven = true,
                verifiedByAdmin = true
            ),
            Donor(
                id = 5,
                donorCode = "LL-7705",
                fullName = "David Wilson",
                email = "david.w@gmail.com",
                phoneNumber = "+91 98800 66543",
                bloodGroup = "AB-",
                city = "Bengaluru",
                address = "Indiranagar, Bengaluru",
                latitude = 12.9784,
                longitude = 77.6408,
                age = 34,
                weightKg = 76.0,
                lastDonationEpoch = now - (20 * dayMs), // 20 days -> INELIGIBLE (COOLDOWN)
                isAvailable = false,
                consentGiven = true,
                verifiedByAdmin = true
            ),
            Donor(
                id = 6,
                donorCode = "LL-7706",
                fullName = "Suresh Babu",
                email = "suresh.babu@gmail.com",
                phoneNumber = "+91 98940 33441",
                bloodGroup = "B-",
                city = "Coimbatore",
                address = "Gandhipuram, Coimbatore",
                latitude = 11.0180,
                longitude = 76.9680,
                age = 40,
                weightKg = 81.0,
                lastDonationEpoch = now - (210 * dayMs), // 210 days -> ELIGIBLE
                isAvailable = true,
                consentGiven = true,
                verifiedByAdmin = true
            ),
            Donor(
                id = 7,
                donorCode = "LL-7707",
                fullName = "Ananya Sharma",
                email = "ananya.s@yahoo.com",
                phoneNumber = "+91 97910 55432",
                bloodGroup = "A-",
                city = "Chennai",
                address = "T. Nagar, Chennai",
                latitude = 13.0418,
                longitude = 80.2341,
                age = 24,
                weightKg = 52.0,
                lastDonationEpoch = now - (95 * dayMs), // 95 days -> ELIGIBLE
                isAvailable = true,
                consentGiven = true,
                verifiedByAdmin = true
            ),
            Donor(
                id = 8,
                donorCode = "LL-7708",
                fullName = "Rajesh Kannan",
                email = "rajesh.kannan@gmail.com",
                phoneNumber = "+91 98427 11990",
                bloodGroup = "AB+",
                city = "Salem",
                address = "Suramangalam, Salem",
                latitude = 11.6850,
                longitude = 78.1180,
                age = 38,
                weightKg = 74.0,
                lastDonationEpoch = now - (180 * dayMs), // 180 days -> ELIGIBLE
                isAvailable = true,
                consentGiven = true,
                verifiedByAdmin = true
            )
        )
        database.donorDao().insertAll(donors)

        // 4. Seed Active SOS Broadcasts
        val requests = listOf(
            EmergencyRequest(
                id = 1,
                patientName = "Suresh Kumar",
                bloodGroup = "O+",
                component = "Whole Blood",
                unitsNeeded = 3,
                hospital = "Apollo General",
                city = "Chennai",
                contactPhone = "+91 93812 34567",
                urgencyLevel = "Critical",
                status = "OPEN",
                createdAtEpoch = now - 45 * 60000L,
                notes = "Major bypass surgery scheduled immediately. Family matching failed."
            ),
            EmergencyRequest(
                id = 2,
                patientName = "Deepa Raman",
                bloodGroup = "O-",
                component = "Packed RBC",
                unitsNeeded = 2,
                hospital = "Manipal Hospital",
                city = "Salem",
                contactPhone = "+91 94432 11029",
                urgencyLevel = "Critical",
                status = "OPEN",
                createdAtEpoch = now - 90 * 60000L,
                notes = "Emergency trauma casualty. Immediate rare negative blood required."
            ),
            EmergencyRequest(
                id = 3,
                patientName = "Vigneshwaran P",
                bloodGroup = "B+",
                component = "Platelets",
                unitsNeeded = 4,
                hospital = "Ganga Trauma Center",
                city = "Coimbatore",
                contactPhone = "+91 98940 55432",
                urgencyLevel = "Urgent",
                status = "OPEN",
                createdAtEpoch = now - 180 * 60000L,
                notes = "Dengue platelet drop to 18,000. Single Donor Platelet needed."
            )
        )
        database.emergencyRequestDao().insertAll(requests)

        // 5. Seed Activity Logs
        val logs = listOf(
            ActivityLog(
                actionType = "SYSTEM_INITIALIZATION",
                description = "Database bootstrapped with 5 accredited blood banks, stocks, and verified donors.",
                ipAddress = "127.0.0.1",
                timestampEpoch = now - 3600000L
            ),
            ActivityLog(
                actionType = "STOCK_ALLOCATION",
                description = "Automated stock telemetry calibrated across Salem and Chennai zones.",
                ipAddress = "192.168.1.104",
                timestampEpoch = now - 1800000L
            ),
            ActivityLog(
                actionType = "SOS_BROADCAST",
                description = "Broadcast alert dispatched for Patient Suresh Kumar (O+, 3 Units) at Apollo General.",
                ipAddress = "192.168.1.155",
                timestampEpoch = now - 2700000L
            )
        )
        database.activityLogDao().insertAll(logs)
    }
}
