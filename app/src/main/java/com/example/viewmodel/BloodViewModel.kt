package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.engine.DataEngineService
import com.example.data.local.BloodDatabase
import com.example.data.local.DatabaseInitializer
import com.example.data.model.ActivityLog
import com.example.data.model.BloodBank
import com.example.data.model.BloodStock
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import com.example.data.repository.BloodRepository
import com.example.util.HaversineUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class BloodViewModel(application: Application) : AndroidViewModel(application) {

    private val database = BloodDatabase.getInstance(application)
    private val repository = BloodRepository(database)

    init {
        viewModelScope.launch {
            DatabaseInitializer.populateIfEmpty(database)
        }
    }

    // Reference Coordinates (e.g. default user center in Salem / Chennai)
    var userLatitude: Double = 11.6643 // Salem
    var userLongitude: Double = 78.1460

    // Database Flows
    val bloodBanks: StateFlow<List<BloodBank>> = repository.allBloodBanks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stocks: StateFlow<List<BloodStock>> = repository.allStocks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val donors: StateFlow<List<Donor>> = repository.allDonors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val openRequests: StateFlow<List<EmergencyRequest>> = repository.openRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRequests: StateFlow<List<EmergencyRequest>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activityLogs: StateFlow<List<ActivityLog>> = repository.recentActivityLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and filter states
    private val _selectedCity = MutableStateFlow("All")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _selectedBloodGroup = MutableStateFlow("All")
    val selectedBloodGroup: StateFlow<String> = _selectedBloodGroup.asStateFlow()

    private val _selectedComponent = MutableStateFlow("All")
    val selectedComponent: StateFlow<String> = _selectedComponent.asStateFlow()

    private val _onlyEligible = MutableStateFlow(false)
    val onlyEligible: StateFlow<Boolean> = _onlyEligible.asStateFlow()

    // Revealed Contacts (upon accepting SOS alert)
    private val _revealedRequestIds = MutableStateFlow<Set<Long>>(emptySet())
    val revealedRequestIds: StateFlow<Set<Long>> = _revealedRequestIds.asStateFlow()

    // Mode A API Output state
    private val _modeAJsonOutput = MutableStateFlow("")
    val modeAJsonOutput: StateFlow<String> = _modeAJsonOutput.asStateFlow()

    private val _templateJsonOutput = MutableStateFlow("")
    val templateJsonOutput: StateFlow<String> = _templateJsonOutput.asStateFlow()

    // Filtered Donors with Haversine distance
    val filteredDonors: StateFlow<List<Pair<Donor, Double>>> = combine(
        donors,
        _selectedCity,
        _selectedBloodGroup,
        _onlyEligible
    ) { donorList, city, bg, onlyElig ->
        donorList.filter { donor ->
            val cityMatches = city == "All" || donor.city.equals(city, ignoreCase = true)
            val bgMatches = bg == "All" || donor.bloodGroup.equals(bg, ignoreCase = true)
            val eligMatches = !onlyElig || donor.isEligible()
            cityMatches && bgMatches && eligMatches
        }.map { donor ->
            val dist = HaversineUtil.calculateDistanceKm(
                userLatitude, userLongitude,
                donor.latitude, donor.longitude
            )
            Pair(donor, dist)
        }.sortedBy { it.second }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedCity(city: String) {
        _selectedCity.value = city
        refreshApiJson()
    }

    fun setSelectedBloodGroup(group: String) {
        _selectedBloodGroup.value = group
        refreshApiJson()
    }

    fun setSelectedComponent(comp: String) {
        _selectedComponent.value = comp
        refreshApiJson()
    }

    fun toggleOnlyEligible(eligible: Boolean) {
        _onlyEligible.value = eligible
    }

    fun refreshApiJson() {
        val city = if (_selectedCity.value == "All") "Chennai" else _selectedCity.value
        val bg = if (_selectedBloodGroup.value == "All") "O+" else _selectedBloodGroup.value
        val comp = if (_selectedComponent.value == "All") "Whole Blood" else _selectedComponent.value

        _modeAJsonOutput.value = DataEngineService.generateModeAResponse(
            bloodGroup = bg,
            location = city,
            component = comp,
            bloodBanks = bloodBanks.value,
            stocks = stocks.value,
            donors = donors.value,
            requests = openRequests.value,
            userLat = userLatitude,
            userLon = userLongitude
        )

        _templateJsonOutput.value = DataEngineService.generateDistrictTemplateResponse(
            location = if (_selectedCity.value == "All") "Salem" else _selectedCity.value,
            bloodGroup = if (_selectedBloodGroup.value == "All") "O-" else _selectedBloodGroup.value,
            bloodBanks = bloodBanks.value,
            donors = donors.value
        )
    }

    fun createEmergencyRequest(
        patientName: String,
        bloodGroup: String,
        component: String,
        unitsNeeded: Int,
        hospital: String,
        city: String,
        contactPhone: String,
        urgencyLevel: String,
        notes: String
    ) {
        viewModelScope.launch {
            val req = EmergencyRequest(
                patientName = patientName.trim(),
                bloodGroup = bloodGroup,
                component = component,
                unitsNeeded = unitsNeeded,
                hospital = hospital.trim(),
                city = city.trim(),
                contactPhone = contactPhone.trim(),
                urgencyLevel = urgencyLevel,
                notes = notes.trim()
            )
            repository.createEmergencyRequest(req)
            refreshApiJson()
        }
    }

    fun acceptAlert(requestId: Long, patientName: String) {
        viewModelScope.launch {
            repository.acceptEmergencyAlert(requestId, "DONOR-YOU", patientName)
            _revealedRequestIds.value = _revealedRequestIds.value + requestId
            refreshApiJson()
        }
    }

    fun fulfillRequest(requestId: Long, patientName: String) {
        viewModelScope.launch {
            repository.fulfillEmergencyRequest(requestId, patientName)
            refreshApiJson()
        }
    }

    fun registerDonor(
        fullName: String,
        email: String,
        phone: String,
        bloodGroup: String,
        city: String,
        address: String,
        age: Int,
        weightKg: Double,
        daysSinceLastDonation: Int,
        chronicIllness: Boolean,
        medication: Boolean,
        recentSurgery: Boolean,
        consentGiven: Boolean
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val lastDonationEpoch = if (daysSinceLastDonation >= 0) {
                now - (daysSinceLastDonation * TimeUnit.DAYS.toMillis(1))
            } else 0L

            val code = "LL-" + (7700 + (donors.value.size + 1))
            val donor = Donor(
                donorCode = code,
                fullName = fullName.trim(),
                email = email.trim(),
                phoneNumber = phone.trim(),
                bloodGroup = bloodGroup,
                city = city.trim(),
                address = address.trim(),
                latitude = userLatitude + (Math.random() - 0.5) * 0.05,
                longitude = userLongitude + (Math.random() - 0.5) * 0.05,
                age = age,
                weightKg = weightKg,
                lastDonationEpoch = lastDonationEpoch,
                isAvailable = daysSinceLastDonation >= 90 && !chronicIllness && !recentSurgery && age in 18..65 && weightKg >= 50.0,
                chronicIllness = chronicIllness,
                medication = medication,
                recentSurgery = recentSurgery,
                consentGiven = consentGiven,
                verifiedByAdmin = true
            )
            repository.registerDonor(donor)
            refreshApiJson()
        }
    }

    fun adjustStock(bankId: Long, bankName: String, bloodGroup: String, component: String, delta: Int) {
        viewModelScope.launch {
            repository.adjustStockUnits(bankId, bankName, bloodGroup, component, delta)
            refreshApiJson()
        }
    }

    fun toggleDonorStatus(donor: Donor) {
        viewModelScope.launch {
            repository.toggleDonorStatus(donor)
            refreshApiJson()
        }
    }

    fun toggleDonorVerification(donor: Donor) {
        viewModelScope.launch {
            repository.toggleDonorVerification(donor)
            refreshApiJson()
        }
    }
}
