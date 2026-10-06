package com.example

import com.example.data.engine.DataEngineService
import com.example.data.model.BloodBank
import com.example.data.model.BloodGroup
import com.example.data.model.Donor
import com.example.util.HaversineUtil
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleUnitTest {

    @Test
    fun haversineDistance_isCalculatedAccurately() {
        // Distance between Salem (11.6643, 78.1460) and Chennai (13.0827, 80.2707) is approx 280-290 km
        val distance = HaversineUtil.calculateDistanceKm(11.6643, 78.1460, 13.0827, 80.2707)
        assertTrue("Distance should be between 270 and 310 km", distance in 270.0..310.0)
    }

    @Test
    fun medicalCooldown_under90DaysIsIneligible() {
        val now = System.currentTimeMillis()
        val cooldownActiveDonor = Donor(
            donorCode = "LL-9901",
            fullName = "Test Ineligible",
            email = "test@example.com",
            phoneNumber = "+91 9876543210",
            bloodGroup = "O+",
            city = "Salem",
            address = "Test Street",
            latitude = 11.66,
            longitude = 78.14,
            age = 25,
            weightKg = 60.0,
            lastDonationEpoch = now - TimeUnit.DAYS.toMillis(45), // 45 days ago (< 90)
            isAvailable = false
        )

        assertFalse("Donor within 90 days cooldown must be ineligible", cooldownActiveDonor.isEligible())
        assertTrue("Status string should state Cooldown", cooldownActiveDonor.getEligibilityStatusString().contains("Cooldown"))
    }

    @Test
    fun medicalCooldown_over90DaysIsEligible() {
        val now = System.currentTimeMillis()
        val eligibleDonor = Donor(
            donorCode = "LL-9902",
            fullName = "Test Eligible",
            email = "test@example.com",
            phoneNumber = "+91 9876543210",
            bloodGroup = "O+",
            city = "Salem",
            address = "Test Street",
            latitude = 11.66,
            longitude = 78.14,
            age = 25,
            weightKg = 60.0,
            lastDonationEpoch = now - TimeUnit.DAYS.toMillis(100), // 100 days ago (> 90)
            isAvailable = true
        )

        assertTrue("Donor beyond 90 days cooldown must be eligible", eligibleDonor.isEligible())
        assertEquals("Eligible", eligibleDonor.getEligibilityStatusString())
    }

    @Test
    fun bloodCompatibility_universalDonorAndRecipient() {
        // O- can donate to all groups
        BloodGroup.entries.forEach { recipient ->
            assertTrue("O- should donate to ${recipient.label}", BloodGroup.O_NEG.canDonateTo(recipient))
        }

        // AB+ can receive from all groups
        BloodGroup.entries.forEach { donor ->
            assertTrue("AB+ should receive from ${donor.label}", BloodGroup.AB_POS.canReceiveFrom(donor))
        }
    }

    @Test
    fun privacyMatrix_masksPhoneNumber() {
        val donor = Donor(
            donorCode = "LL-7701",
            fullName = "Arun Kumar",
            email = "arun@example.com",
            phoneNumber = "+91 9876543210",
            bloodGroup = "O-",
            city = "Chennai",
            address = "12 Road",
            latitude = 13.08,
            longitude = 80.27,
            age = 29,
            weightKg = 68.0
        )

        val masked = donor.getMaskedPhone()
        assertTrue("Phone number must end with XXXXX", masked.endsWith("XXXXX"))
        assertFalse("Full phone number must not be exposed", masked.contains("43210"))
    }

    @Test
    fun dataEngineService_generatesValidModeAJson() {
        val jsonStr = DataEngineService.generateModeAResponse(
            bloodGroup = "O+",
            location = "Chennai",
            component = "Whole Blood",
            bloodBanks = emptyList(),
            stocks = emptyList(),
            donors = emptyList(),
            requests = emptyList()
        )

        val obj = JSONObject(jsonStr)
        assertEquals("success", obj.getString("searchStatus"))
        assertTrue(obj.has("queryParameters"))
        assertTrue(obj.has("bloodBanks"))
        assertTrue(obj.has("emergencyDonors"))
        assertTrue(obj.has("activeSOSBroadcasts"))
        assertTrue(obj.has("education"))
    }
}
